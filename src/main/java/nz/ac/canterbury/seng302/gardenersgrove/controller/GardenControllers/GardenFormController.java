package nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers;

import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
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
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/** Controller class responsible for handling garden-related HTTP requests. */
@Controller
public class GardenFormController {
  Logger logger = LoggerFactory.getLogger(GardenFormController.class);
  private final GardenService gardenService;
  private final GardenerFormService gardenerFormService;
  private Gardener gardener;

  /**
   * Constructor used to create a new instance of the GardenFormController. Autowires a
   * various service objects
   *
   * @param gardenService the garden service used to interact with the database
   * @param gardenerFormService the gardener form service used to interact with the database
   */
  @Autowired
  public GardenFormController(
      GardenService gardenService,
      GardenerFormService gardenerFormService) {
    this.gardenService = gardenService;
    this.gardenerFormService = gardenerFormService;
  }

  /**
   * Retrieve an optional of a gardener using the current authentication We will always have to
   * check whether the gardener was retrieved in the calling method, so the return type was left as
   * an optional
   *
   * @return An optional of the requested gardener
   */
  public Optional<Gardener> getGardenerFromAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    return gardenerFormService.findByEmail(currentUserEmail);
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

    Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
    gardenerOptional.ifPresent(value -> gardener = value);
    List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());

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
   * @param suburb The suburb of the garden.
   * @param city The city of the garden.
   * @param country The country of the garden.
   * @param postcode The postcode of the garden.
   * @param size The size of the garden.
   * @param description The garden description.
   * @param redirect the uri to redirect to if the cancel button is pressed
   * @param model The model for passing data to the view.
   * @return The name of the template for displaying the garden form.
   */
  @PostMapping("gardens/form")
  public String submitForm(
      @RequestParam(name = "name") String name,
      @RequestParam(name = "location") String location,
      @RequestParam(name = "suburb") String suburb,
      @RequestParam(name = "city") String city,
      @RequestParam(name = "country") String country,
      @RequestParam(name = "postcode") String postcode,
      @RequestParam(name = "size") String size,
      @RequestParam(name = "description") String description,
      @RequestParam(name = "redirect") String redirect,
      Model model) {
    logger.info("POST /form");
    String validatedName = ValidityChecker.validateGardenName(name);
    String validatedAddress = ValidityChecker.validateGardenAddress(location);
    String validatedSuburb = ValidityChecker.validateGardenSuburb(suburb);
    String validatedCity = ValidityChecker.validateGardenCity(city);
    String validatedCountry = ValidityChecker.validateGardenCountry(country);
    String validatedPostcode = ValidityChecker.validateGardenPostcode(postcode);
    String validatedSize = ValidityChecker.validateGardenSize(size);
    String validatedDescription = ValidityChecker.validateGardenDescription(description);
    Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();

    boolean isValid = true;

    gardenerOptional.ifPresent(value -> gardener = value);

    String newLocation;
    newLocation = Objects.requireNonNullElse(location, "");

    String newSuburb;
    newSuburb = Objects.requireNonNullElse(suburb, "");

    String newPostcode;
    newPostcode = Objects.requireNonNullElse(postcode, "");

    if (!Objects.equals(name, validatedName)) {
      model.addAttribute("nameError", validatedName);
      isValid = false;
    }
    if (!Objects.equals(size.replace(',', '.'), validatedSize)) {
      model.addAttribute("sizeError", validatedSize);
      isValid = false;
    }
    if (!Objects.equals(description, validatedDescription)) {
      model.addAttribute("descriptionError", validatedDescription);
      isValid = false;
    }

    if(!Objects.equals(location, validatedAddress)) {
      model.addAttribute("locationError", validatedAddress);
      isValid = false;
    }
    if(!Objects.equals(suburb, validatedSuburb)) {
      model.addAttribute("suburbError", validatedSuburb);
      isValid = false;
    }
    if(!Objects.equals(city, validatedCity)) {
      model.addAttribute("cityError", validatedCity);
      isValid = false;
    }
    if(!Objects.equals(country, validatedCountry)) {
      model.addAttribute("countryError", validatedCountry);
      isValid = false;
    }
    if(!Objects.equals(postcode, validatedPostcode)) {
      model.addAttribute("postcodeError", validatedPostcode);
      isValid = false;
    }

    if (isValid) {
        Garden garden;
        if (Objects.equals(size.trim(), "")) {
          garden = gardenService.addGarden(new Garden(name ,newLocation.trim(), newSuburb.trim(), city, country, newPostcode.trim(), gardener, description));
        } else {
          garden = gardenService.addGarden((new Garden(name, newLocation.trim(), newSuburb.trim(), city, country, newPostcode.trim(), new BigDecimal(validatedSize).stripTrailingZeros().toPlainString(), gardener, description)));
        }
      return "redirect:/gardens/details?gardenId=" + garden.getId();

    } else {
      List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());
      model.addAttribute("gardens", gardens);
      model.addAttribute("requestURI", redirect);
      model.addAttribute("name", name);
      model.addAttribute("location", newLocation);
      model.addAttribute("suburb", newSuburb);
      model.addAttribute("city", city);
      model.addAttribute("country", country);
      model.addAttribute("postcode", newPostcode);
      model.addAttribute("size", size);
      model.addAttribute("description", description);
      return "gardensFormTemplate";
    }
  }
}
