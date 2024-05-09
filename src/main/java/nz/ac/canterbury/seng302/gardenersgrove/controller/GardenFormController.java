package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RelationshipService;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
  public String getGardenHome(@RequestParam(name="user", required = false) String user, Model model, HttpServletRequest request, HttpServletResponse response) {
    logger.info("GET /gardens/main");
    // Prevent caching of the page so that we always reload it when we reach it (mainly for when you use the browser back button)
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
    response.setHeader("Expires", "0"); // Proxies

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    logger.info("Authentication: " + authentication);
    String currentUserEmail = authentication.getName();
    Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail(currentUserEmail);
    if (gardenerOptional.isPresent()) {
      gardener = gardenerOptional.get();
    }

    List<Garden> gardens;
    if(user == null) {
      gardens = gardenService.getGardensByGardenerId(gardener.getId());
      model.addAttribute("gardener", gardener);
    } else {
      Optional<Gardener> friend = gardenerFormService.findById(parseLong(user, 10));
      if(friend.isPresent() && relationshipService.getCurrentUserRelationships(gardener.getId()).contains(friend.get())) {
        gardens = gardenService.getGardensByGardenerId(parseLong(user, 10));
        model.addAttribute("gardener", friend.get());
      } else {
        return "redirect:/gardens";
      }

    }
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
  private final GardenerFormService gardenerFormService;
  private final RelationshipService relationshipService;
  private Gardener gardener;

  /**
   * Constructor used to create a new instance of the gardenformcontroller. Autowires a gardenservice object
   * @param gardenService the garden service used to interact with the database
   * @param gardenerFormService - object that is used to interact with the database
   */
  @Autowired
  public GardenFormController(GardenService gardenService, GardenerFormService gardenerFormService, RelationshipService relationshipService) {
    this.gardenService = gardenService;
    this.gardenerFormService = gardenerFormService;
    this.relationshipService = relationshipService;
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

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();

    Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail(currentUserEmail);
    List<Garden> gardens = new ArrayList<>();
    if (gardenerOptional.isPresent()) {
      gardens = gardenService.getGardensByGardenerId(gardenerOptional.get().getId());
    }
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
   * @param redirect the uri to redirect to if the cancel button is pressed
   * @param model The model for passing data to the view.
   * @param authentication Used to check whether the user is authenticated
   * @return The name of the template for displaying the garden form.
   */
  @PostMapping("gardens/form")
  public String submitForm(
      @RequestParam(name = "name") String name,
      @RequestParam(name = "location") String location,
      @RequestParam(name = "size") String size,
      @RequestParam(name = "redirect") String redirect,
      Model model,
      Authentication authentication) {
    logger.info("POST /form");
    String validatedName = ValidityChecker.validateGardenName(name);
    String validatedLocation = ValidityChecker.validateGardenLocation(location);
    String validatedSize = ValidityChecker.validateGardenSize(size);
    String currentUserEmail = authentication.getName();
    boolean isValid = true;

    Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail(currentUserEmail);
    if (gardenerOptional.isPresent()) {
      gardener = gardenerOptional.get();
    }

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
        garden = gardenService.addGarden(new Garden(name, location, gardener));
      } else {
        garden = gardenService.addGarden(new Garden(name, location, new BigDecimal(validatedSize).stripTrailingZeros().toPlainString(), gardener));
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
   * @param uploadError An optional parameter indicating the type of upload error encountered.
   *                    - Can be null if no error occurred.
   * @param errorId    An optional parameter identifying the specific error encountered.
   *                    - Can be null if no error occurred.
   * @param model the model
   * @param request the request used to find the current uri
   * @return The garden details page if the garden exists, else remains on the gardens page
   */
  @GetMapping("gardens/details")
  public String gardenDetails(@RequestParam(name = "gardenId") String gardenId,
                              @RequestParam(name = "uploadError", required = false) String uploadError,
                              @RequestParam(name = "errorId", required = false) String errorId,
                              @RequestParam(name = "userId", required = false) String userId,
                              Model model, HttpServletRequest request) {
    logger.info("GET /gardens/details");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();

    Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail(currentUserEmail);
    List<Garden> gardens = new ArrayList<>();
    if (gardenerOptional.isPresent()) {
      gardens = gardenService.getGardensByGardenerId(gardenerOptional.get().getId());
    }

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

      if(uploadError != null) {
        model.addAttribute("uploadError", uploadError);
        model.addAttribute("errorId", errorId);
      }
      if(userId == null || gardener.getId() == parseLong(userId, 10)) {
        return "gardenDetailsTemplate";
      } else {
        Optional<Gardener> friend = gardenerFormService.findById(parseLong(userId, 10));
        if(friend.isPresent() && relationshipService.getCurrentUserRelationships(gardener.getId()).contains(friend.get())) {
          model.addAttribute("gardener", friend.get());
          return "unauthorizedGardenDetailsTemplate";
        } else {
          return "redirect:/gardens";
        }
      }

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
      Garden existingGarden = gardenService.getGarden(parseLong(gardenId)).get();
      existingGarden.setName(name);
      existingGarden.setLocation(location);

      if (Objects.equals(size.trim(), "")) {
        existingGarden.setSize(null);
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
      model.addAttribute(gardenService.getGarden(parseLong(gardenId)).get());
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

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();

    Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail(currentUserEmail);
    List<Garden> gardens = new ArrayList<>();
    if (gardenerOptional.isPresent()) {
      gardens = gardenService.getGardensByGardenerId(gardenerOptional.get().getId());
    }

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
