package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import static java.lang.Float.parseFloat;
import static java.lang.Long.parseLong;
import java.util.Optional;

/** Controller class responsible for handling garden-related HTTP requests. */
@Controller
public class GardenFormController {
  Logger logger = LoggerFactory.getLogger(GardenFormController.class);


  /**
   * Gets the home page that displays the list of gardens
   * @param model the model for passing attributes to the view
   * @param request the request used to find the current uri
   * @return the gardens template which defines the user interface for the my gardens page
   */
  @GetMapping("/gardens")
  public String getGardenHome(Model model, HttpServletRequest request) {
    logger.info("GET /gardens/main");
    List<Garden> gardens = gardenService.getGardenResults();
    model.addAttribute("gardens", gardens);

    String requestUri = request.getRequestURI();
    String queryString = request.getQueryString();
    if (queryString != null) {
      requestUri = requestUri + "?" + queryString;
    }
    model.addAttribute("requestURI", requestUri);
    return "gardensTemplate";
  }

  private final GardenService gardenService;

  /**
   * Constructor used to create a new instance of the gardenformcontroller. Autowires a gardenservice object
   * @param gardenService the garden service used to interact with the database
   */
  @Autowired
  public GardenFormController(GardenService gardenService) {
    this.gardenService = gardenService;
  }

  /**
   * Displays the form for adding a new garden.
   *
   * @param model The model for passing data to the view.
   * @param redirectUri the uri to redirect to if the cancel button is pressed
   * @return The name of the template for displaying the garden form.
   */
  @GetMapping("gardens/form")
  public String form(@RequestParam(name = "redirect") String redirectUri, Model model) {
    logger.info("GET /form");
    List<Garden> gardens = gardenService.getGardenResults();
    model.addAttribute("gardens", gardens);
    model.addAttribute("requestURI", redirectUri);
    return "gardensFormTemplate";
  }

  /**
   * Handles the submission of the garden form. Uses the ValidityChecker to validate the inputs
   * before submitting the entry.
   *
   * @param name The name of the garden.
   * @param location The location of the garden.
   * @param size The size of the garden.
   * @param model The model for passing data to the view.
   * @param redirect the uri to redirect to if the cancel button is pressed
   * @return The name of the template for displaying the garden form.
   */
  @PostMapping("gardens/form")
  public String submitForm(
      @RequestParam(name = "name") String name,
      @RequestParam(name = "location") String location,
      @RequestParam(name = "size") String size,
      @RequestParam(name = "redirect") String redirect,
      Model model) {
    logger.info("POST /form");
    String validatedName = ValidityChecker.validateGardenName(name);
    String validatedLocation = ValidityChecker.validateGardenLocation(location);
    String validatedSize = ValidityChecker.validateGardenSize(size);

    boolean isValid = true;

    if (!Objects.equals(name, validatedName)) {
      model.addAttribute("nameError", validatedName);
      isValid = false;
    }
    if (!Objects.equals(location, validatedLocation)) {
      model.addAttribute("locationError", validatedLocation);
      isValid = false;
    }
    if (!Objects.equals(size.replace(',', '.'), validatedSize)) {
      model.addAttribute("sizeError", validatedSize);
      isValid = false;
    }

    if (isValid) {
        Garden garden;
      if (Objects.equals(size.trim(), "")) {
        garden = gardenService.addGarden(new Garden(name, location));
      } else {
        garden = gardenService.addGarden(new Garden(name, location, new BigDecimal(validatedSize).stripTrailingZeros().toPlainString()));
      }
      return "redirect:/gardens/details?gardenId=" + garden.getId();
    } else {
      List<Garden> gardens = gardenService.getGardenResults();
      model.addAttribute("gardens", gardens);
      model.addAttribute("requestURI",redirect);
      model.addAttribute("name", name);
      model.addAttribute("location", location);
      model.addAttribute("size", size);
      return "gardensFormTemplate";
    }


  }

  /**
   * Gets the garden based on the id and returns the garden details template
   *
   * @param gardenId the id of the garden to be displayed
   * @param model the model
   * @param request the request used to find the current uri
   * @return The garden details page if the garden exists, else remains on the gardens page
   */
  @GetMapping("gardens/details")
  public String gardenDetails(@RequestParam(name = "gardenId") String gardenId, Model model, HttpServletRequest request) {
    logger.info("GET /gardens/details");
    List<Garden> gardens = gardenService.getGardenResults();
    model.addAttribute("gardens", gardens);

    Optional<Garden> garden = gardenService.getGarden(parseLong(gardenId));
    if (garden.isPresent()) {

      String requestUri = request.getRequestURI();
      String queryString = request.getQueryString();
      if (queryString != null) {
        requestUri = requestUri + "?" + queryString;
      }
      model.addAttribute("requestURI", requestUri);

      model.addAttribute("garden", garden.get());
      return "gardenDetailsTemplate";
    } else {
      return "redirect:/gardens";
    }
  }

  /**
   * Updates the details of a garden in the database based on the details provided in the form
   *
   * @param name The name of the garden
   * @param location the location of the garden
   * @param size the size of the garden
   * @param gardenId the id of the garden to edit
   * @param model the model
   * @return the garden details template
   */
  @PostMapping("gardens/edit")
  public String submitEditForm(
      @RequestParam(name = "name") String name,
      @RequestParam(name = "location") String location,
      @RequestParam(name = "size") String size,
      @RequestParam(name = "gardenId") String gardenId,
      Model model) {
    logger.info("POST gardens/edit");

    String validatedName = ValidityChecker.validateGardenName(name);
    String validatedLocation = ValidityChecker.validateGardenLocation(location);
    String validatedSize = ValidityChecker.validateGardenSize(size);

    boolean isValid = true;
    String returnedTemplate = "redirect:/gardens/details?gardenId=" + gardenId;

    if (!Objects.equals(name, validatedName)) {
      model.addAttribute("nameError", validatedName);
      isValid = false;
    }
    if (!Objects.equals(location, validatedLocation)) {
      model.addAttribute("locationError", validatedLocation);
      isValid = false;
    }
    if (!Objects.equals(size.replace(',', '.'), validatedSize)) {
      model.addAttribute("sizeError", validatedSize);
      isValid = false;
    }

    if (isValid) {
      Garden existingGarden = gardenService.getGarden(Long.parseLong(gardenId)).get();
      existingGarden.setName(name);
      existingGarden.setLocation(location);

      if (Objects.equals(size.trim(), "")) {
        existingGarden.setSize("0");
        gardenService.addGarden(existingGarden);
      } else {
        existingGarden.setSize(new BigDecimal(validatedSize).stripTrailingZeros().toPlainString());
        gardenService.addGarden(existingGarden);
      }
    } else {
      List<Garden> gardens = gardenService.getGardenResults();
      model.addAttribute("gardens", gardens);
      model.addAttribute("name", name);
      model.addAttribute("location", location);
      model.addAttribute("size", size.replace(',', '.'));
      model.addAttribute(gardenService.getGarden(Long.parseLong(gardenId)).get());
      returnedTemplate = "editGardensFormTemplate";
    }
    return returnedTemplate;
  }

  /**
   * Gets the garden to edit by the id and returns the edit garden form template
   *
   * @param gardenId the id of the garden to edit
   * @param model the model
   * @param request the request used to find the current uri
   * @return the edit garden form template
   */
  @GetMapping("gardens/edit")
  public String editGarden(@RequestParam(name = "gardenId") String gardenId, Model model, HttpServletRequest request) {
    logger.info("GET gardens/edit");
    List<Garden> gardens = gardenService.getGardenResults();
    model.addAttribute("gardens", gardens);
    Optional<Garden> garden = gardenService.getGarden(parseLong(gardenId));
    if (garden.isPresent()) {
      String requestUri = request.getRequestURI();
      String queryString = request.getQueryString();
      if (queryString != null) {
        requestUri = requestUri + "?" + queryString;
      }
      model.addAttribute("requestURI", requestUri);
      model.addAttribute("garden", garden.get());
      return "editGardensFormTemplate";
    } else {
      return "redirect:/gardens";
    }
  }
}
