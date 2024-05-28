package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RelationshipService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;
import nz.ac.canterbury.seng302.gardenersgrove.util.TagValidation;
import nz.ac.canterbury.seng302.gardenersgrove.service.WeatherService;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import nz.ac.canterbury.seng302.gardenersgrove.util.WordFilter;
import nz.ac.canterbury.seng302.gardenersgrove.util.NotificationUtil;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.net.URISyntaxException;

import static java.lang.Long.parseLong;

/** Controller class responsible for handling garden-related HTTP requests. */
@Controller
public class GardenFormController {
  Logger logger = LoggerFactory.getLogger(GardenFormController.class);
  private final GardenService gardenService;
  private final GardenerFormService gardenerFormService;
  private final RelationshipService relationshipService;
  private final RequestService requestService;
  private final TagService tagService;
  private Gardener gardener;
  private final WeatherService weatherService;

  /**
   * Constructor used to create a new instance of the gardenformcontroller. Autowires a
   * gardenservice object
   *
   * @param gardenService the garden service used to interact with the database
   * @param gardenerFormService - object that is used to interact with the database
   */
  @Autowired
  public GardenFormController(
      GardenService gardenService,
      GardenerFormService gardenerFormService,
      RelationshipService relationshipService,
      RequestService requestService,
      WeatherService weatherService,
      TagService tagService) {
    this.gardenService = gardenService;
    this.gardenerFormService = gardenerFormService;
    this.relationshipService = relationshipService;
    this.requestService = requestService;
    this.weatherService = weatherService;
    this.tagService = tagService;
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
   * Gets the home page that displays the list of gardens
   *
   * @param model the model for passing attributes to the view
   * @param request the request used to find the current uri
   * @return the gardens template which defines the user interface for the my gardens page
   */
  @GetMapping("/gardens")
  public String getGardenHome(
      @RequestParam(name = "user", required = false) String user,
      Model model,
      HttpServletRequest request,
      HttpServletResponse response) {
    logger.info("GET /gardens/main");
    // Prevent caching of the page so that we always reload it when we reach it (mainly for when you
    // use the browser back button)
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
    response.setHeader("Expires", "0"); // Proxies

    Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
    gardenerOptional.ifPresent(value -> gardener = value);

    List<Garden> gardens;
    if (user == null) {
      gardens = gardenService.getGardensByGardenerId(gardener.getId());
      model.addAttribute("gardener", gardener);
    } else {
      Optional<Gardener> friend = gardenerFormService.findById(parseLong(user, 10));
      if (friend.isPresent()
          && relationshipService
              .getCurrentUserRelationships(gardener.getId())
              .contains(friend.get())) {
        gardens = gardenService.getGardensByGardenerId(parseLong(user, 10));
        model.addAttribute("gardener", friend.get());
        List<Garden> userGardens;
        userGardens = gardenService.getGardensByGardenerId(gardener.getId());
        model.addAttribute("userGardens", userGardens);
      } else {
        return "redirect:/gardens";
      }
    }
    model.addAttribute("gardens", gardens);
    model.addAttribute("requestURI", requestService.getRequestURI(request));
    return "gardensTemplate";
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
   * @return The name of thegetName template for displaying the garden form.
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

    if (Objects.equals(city, "")) {
      model.addAttribute("cityError", "City is required.");
      isValid = false;
    }
    if(Objects.equals(country, "")) {
      model.addAttribute("countryError", "Country is required.");
      isValid = false;
    }


    if (isValid) {
      Garden garden;
      if (Objects.equals(size.trim(), "")) {
        garden = gardenService.addGarden(new Garden(name,  newLocation, newSuburb, city, country, newPostcode, gardener, description));
      } else {
        garden =
            gardenService.addGarden(
                new Garden(
                    name,
                        newLocation, newSuburb, city, country, newPostcode,
                    new BigDecimal(validatedSize).stripTrailingZeros().toPlainString(),
                    gardener, description));
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

  /**
   * Gets the garden based on the id and returns the garden details template
   *
   * @param gardenId the id of the garden to be displayed
   * @param uploadError An optional parameter indicating the type of upload error encountered. - Can
   *     be null if no error occurred.
   * @param errorId An optional parameter identifying the specific error encountered. - Can be null
   *     if no error occurred.
   * @param model the model
   * @param request the request used to find the current uri
   * @return The garden details page if the garden exists, else remains on the gardens page
   */
  @GetMapping("gardens/details")
  public String gardenDetails(
      @RequestParam(name = "gardenId") String gardenId,
      @RequestParam(name = "uploadError", required = false) String uploadError,
      @RequestParam(name = "errorId", required = false) String errorId,
      @RequestParam(name = "userId", required = false) String userId,
      @RequestParam(name = "showModal", required = false) String showModal,
      @RequestParam(name = "tagValid", required = false) String tagValid,
      Model model,
      HttpServletRequest request)
      throws IOException, URISyntaxException {
    logger.info("GET /gardens/details");

    Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
    List<Garden> gardens = new ArrayList<>();
    if (gardenerOptional.isPresent()) {
      gardener = gardenerOptional.get();
      gardens = gardenService.getGardensByGardenerId(gardener.getId());
    }

    model.addAttribute("gardens", gardens);

    if (gardenId == null) {
      return "redirect:/gardens";
    }
    Optional<Garden> garden = gardenService.getGarden(parseLong(gardenId));
    if (garden.isPresent()) {

      model.addAttribute("requestURI", requestService.getRequestURI(request));

      model.addAttribute("garden", garden.get());
      model.addAttribute("tags", tagService.getTags(parseLong(gardenId)));
      if (uploadError != null) {
        model.addAttribute("uploadError", uploadError);
        model.addAttribute("errorId", errorId);
      }
      if (tagValid != null) {
        model.addAttribute("tagValid", tagValid);
      }
      if (showModal != null && showModal.equals("true")) {
        model.addAttribute("showModal", true);
      }
      if (userId == null || gardener.getId() == parseLong(userId, 10)) {
        Weather currentWeather = weatherService.getWeather(garden.get().getLocation());
        PrevWeather prevWeathers = weatherService.getPrevWeather(garden.get().getLocation());
        if (currentWeather != null && prevWeathers != null) {
          model.addAttribute("date", currentWeather.getDate());
          model.addAttribute("temperature", currentWeather.getTemperature());
          model.addAttribute("weatherImage", currentWeather.getWeatherImage());
          model.addAttribute("weatherDescription", currentWeather.getWeatherDescription());
          model.addAttribute("humidity", currentWeather.getHumidity());
          model.addAttribute("forecastDates", currentWeather.getForecastDates());
          model.addAttribute("forecastTemperature", currentWeather.getForecastTemperatures());
          model.addAttribute("forecastWeatherImage", currentWeather.getForecastImages());
          model.addAttribute(
              "forecastWeatherDescription", currentWeather.getForecastDescriptions());
          model.addAttribute("forecastHumidities", currentWeather.getForecastHumidities());
          LocalDate currentDate = LocalDate.now();
          LocalDate lastNotifiedDate = garden.get().getLastNotified();
          if (lastNotifiedDate != null && lastNotifiedDate.equals(currentDate)) {
            model.addAttribute("wateringTip", null);
          }  else {
            if (lastNotifiedDate != null) {
              gardenService.updateLastNotifiedbyId(parseLong(gardenId), null);
            }
            String wateringTip = NotificationUtil.generateWateringTip(currentWeather, prevWeathers);
            model.addAttribute("wateringTip", wateringTip);
          }


        }
        return "gardenDetailsTemplate";
      } else {
        Optional<Gardener> friend = gardenerFormService.findById(parseLong(userId, 10));
        if (friend.isPresent()
            && relationshipService
                .getCurrentUserRelationships(gardener.getId())
                .contains(friend.get())) {
          model.addAttribute("gardener", friend.get());
          model.addAttribute("tags", tagService.getTags(parseLong(gardenId)));
          return "unauthorizedGardenDetailsTemplate";
        } else {
          return "redirect:/gardens";
        }
      }
    }
    return "redirect:/gardens";
  }

  /**
   * Posts a form response with a new Gardener
   * @param isGardenPublic is public checkbox selected
   * @param gardenId id for Garden being viewed
   * @param model (map-like) representation of isGardenPublic boolean for use in thymeleaf,
   *              with value being set to relevant parameter provided
   * @return thymeleaf Garden Details form template
   */
  @PostMapping("/gardens/details")
  public String submitForm(@RequestParam(name = "isGardenPublic", required = false) boolean isGardenPublic,
                           @RequestParam(name = "gardenId") String gardenId,

                           @RequestParam(name = "uploadError", required = false) String uploadError,
                           @RequestParam(name = "errorId", required = false) String errorId,
                           @RequestParam(name = "userId", required = false) String userId,

                           Model model, HttpServletRequest request) {
    logger.info("POST /gardens/details");

    model.addAttribute("isGardenPublic", isGardenPublic);

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail(currentUserEmail);

    if (gardenId == null || gardenerOptional.isEmpty()) {
      return "redirect:/gardens";
    }
    List<Garden> gardens = gardenService.getGardensByGardenerId(gardenerOptional.get().getId());
    model.addAttribute("gardens", gardens);
    Optional<Garden> garden = gardenService.getGarden(parseLong(gardenId));
    if (garden.isPresent()) {

      Garden existingGarden = garden.get();
      existingGarden.setIsGardenPublic(isGardenPublic);
      gardenService.addGarden(existingGarden);

      String requestUri = requestService.getRequestURI(request);
      model.addAttribute("requestURI", requestUri);

      model.addAttribute("garden", garden.get());

      if(uploadError != null) {
        model.addAttribute("uploadError", uploadError);
        model.addAttribute("errorId", errorId);
      }
      if(userId == null || gardener.getId() == parseLong(userId, 10)) {
        return "redirect:/gardens/details?gardenId=" + gardenId;
      } else {
        Optional<Gardener> friend = gardenerFormService.findById(parseLong(userId, 10));
        if(friend.isPresent() && relationshipService.getCurrentUserRelationships(gardener.getId()).contains(friend.get())) {
          model.addAttribute("gardener", friend.get());
          return "unauthorizedGardenDetailsTemplate";
        } else {
          return "redirect:/gardens";
        }
      }
    }
    return "redirect:/gardens";
  }
  @PostMapping("/gardens/details/dismissNotification")
  public String submitNotificationForm(
                           @RequestParam(name = "gardenId") String gardenId) {
    logger.info("POST /gardens/details/dismissNotification");
    if (gardenId == null) {
      return "redirect:/gardens";
    }

    Optional<Garden> garden = gardenService.getGarden(parseLong(gardenId));
    if (garden.isPresent()) {
      LocalDate date = LocalDate.now();
      gardenService.updateLastNotifiedbyId(parseLong(gardenId), date);
    }


     return "redirect:/gardens/details?gardenId=" + gardenId;
  }

  /**
   * Updates the details of a garden in the database based on the details provided in the form
   *
   * @param name The name of the garden
   * @param location the location of the garden
   * @param suburb The suburb of the garden.
   * @param city The city of the garden.
   * @param country The country of the garden.
   * @param postcode The postcode of the garden.
   * @param size the size of the garden
   * @param description the garden description
   * @param gardenId the id of the garden to edit
   * @param model the model
   * @return the garden details template
   */
  @PostMapping("gardens/edit")
  public String submitEditForm(
      @RequestParam(name = "name") String name,
      @RequestParam(name = "location") String location,
      @RequestParam(name = "suburb") String suburb,
      @RequestParam(name = "city") String city,
      @RequestParam(name = "country") String country,
      @RequestParam(name = "postcode") String postcode,
      @RequestParam(name = "size") String size,
      @RequestParam(name = "description") String description,
      @RequestParam(name = "gardenId") String gardenId,
      Model model,
      HttpServletRequest request) {
    logger.info("POST gardens/edit");
    String validatedName = ValidityChecker.validateGardenName(name);
    String validatedSize = ValidityChecker.validateGardenSize(size);
    String validatedDescription = ValidityChecker.validateGardenDescription(description);

    Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
    List<Garden> gardens = new ArrayList<>();
    if (gardenerOptional.isPresent()) {
      gardener = gardenerOptional.get();
      gardens = gardenService.getGardensByGardenerId(gardenerOptional.get().getId());
    }

    boolean isValid = true;
    String returnedTemplate = "redirect:/gardens/details?gardenId=" + gardenId;

    if (!Objects.equals(name, validatedName)) {
      model.addAttribute("nameError", validatedName);
      isValid = false;
    }
    if (!Objects.equals(size.replace(',', '.'), validatedSize)) {
      model.addAttribute("sizeError", validatedSize);
      isValid = false;
    }
    if (Objects.equals(city, "")) {
      model.addAttribute("cityError", "City is required.");
      isValid = false;
    }
    if(Objects.equals(country, "")) {
      model.addAttribute("countryError", "Country is required.");
      isValid = false;
    }
      if (!Objects.equals(description, validatedDescription)) {
          model.addAttribute("descriptionError", validatedDescription);
          isValid = false;
      }

    if (isValid) {
      Garden existingGarden = gardenService.getGarden(parseLong(gardenId)).get();
      logger.info(existingGarden.getLocation());
      if (!existingGarden.getLocation().equals(location)){
        gardenService.updateLastNotifiedbyId(parseLong(gardenId), null);
      }

      existingGarden.setName(name);
      existingGarden.setLocation(location);
      existingGarden.setSuburb(suburb);
      existingGarden.setCity(city);
      existingGarden.setCountry(country);
      existingGarden.setPostcode(postcode);
      existingGarden.setDescription(description);

      if (Objects.equals(size.trim(), "")) {
        existingGarden.setSize(null);
        gardenService.addGarden(existingGarden);
      } else {
        existingGarden.setSize(new BigDecimal(validatedSize).stripTrailingZeros().toPlainString());
        gardenService.addGarden(existingGarden);
      }
    } else {
      model.addAttribute("gardens", gardens);
      model.addAttribute("name", name);
      model.addAttribute("location", location);
      model.addAttribute("description", description);
      model.addAttribute("suburb", suburb);
      model.addAttribute("city", city);
      model.addAttribute("country", country);
      model.addAttribute("postcode", postcode);
      model.addAttribute("size", size.replace(',', '.'));
      model.addAttribute(gardenService.getGarden(parseLong(gardenId)).get());
      model.addAttribute("requestURI", requestService.getRequestURI(request));
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
  public String editGarden(
      @RequestParam(name = "gardenId") String gardenId, Model model, HttpServletRequest request) {
    logger.info("GET gardens/edit");

    Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
    List<Garden> gardens = new ArrayList<>();
    if (gardenerOptional.isPresent()) {
      gardener = gardenerOptional.get();
      gardens = gardenService.getGardensByGardenerId(gardener.getId());
    }

    model.addAttribute("gardens", gardens);
    Optional<Garden> garden = gardenService.getGarden(parseLong(gardenId));
    if (garden.isPresent()) {
      model.addAttribute("requestURI", requestService.getRequestURI(request));
      model.addAttribute("garden", garden.get());
      return "editGardensFormTemplate";
    } else {
      return "redirect:/gardens";
    }
  }

  /**
   * Adds an existing tag to the garden or creates a new tag to add
   *
   * @param tag the tag to be added
   * @param id the garden id
   * @param model the model
   * @return redirects back to the garden details or add tag modal based on the tag validation
   */
  @PostMapping("gardens/addTag")
  public String addTag(
      @RequestParam(name = "tag-input") String tag,
      @RequestParam(name = "gardenId") long id,
      Model model) throws IOException, URISyntaxException {

    logger.info("POST /addTag");
    tag = tag.strip();
    TagValidation tagValidation = new TagValidation(tagService);
    Optional<Garden> gardenOptional = gardenService.getGarden(id);
    if (gardenOptional.isEmpty()) {
      return "redirect:/gardens";
    }
    Garden garden = gardenOptional.get();
    Optional<String> validTagError = tagValidation.validateTag(tag);
    Optional<String> tagInUse = tagValidation.checkTagInUse(tag, garden);

    Weather currentWeather = weatherService.getWeather(garden.getLocation());
    model.addAttribute("date", currentWeather.getDate());
    model.addAttribute("temperature", currentWeather.getTemperature());
    model.addAttribute("weatherImage", currentWeather.getWeatherImage());
    model.addAttribute("weatherDescription", currentWeather.getWeatherDescription());
    model.addAttribute("humidity", currentWeather.getHumidity());
    model.addAttribute("forecastDates", currentWeather.getForecastDates());
    model.addAttribute("forecastTemperature", currentWeather.getForecastTemperatures());
    model.addAttribute("forecastWeatherImage", currentWeather.getForecastImages());
    model.addAttribute(
            "forecastWeatherDescription", currentWeather.getForecastDescriptions());
    model.addAttribute("forcastHumidities", currentWeather.getForecastHumidities());
    model.addAttribute("gardens", gardenService.getGardensByGardenerId(garden.getGardener().getId()));

    if (validTagError.isPresent()) {
      model.addAttribute("tagValid", validTagError.get());
      model.addAttribute("tag", tag);
      model.addAttribute("tags", tagService.getTags(garden.getId()));
      model.addAttribute("garden", garden);
      return "gardenDetailsTemplate";
    }

    if (tagInUse.isEmpty()) {
      if (!WordFilter.doesContainBadWords(tag)) {
        Tag newTag = new Tag(tag, gardenService.getGarden(id).get());
        tagService.addTag(newTag);
        logger.info("Tag '{}' passes moderation checks", tag);
      } else {
        model.addAttribute("garden", garden);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", tagService.getTags(garden.getId()));
        model.addAttribute("tagValid", "Submitted tag fails moderation requirements");
        return "gardenDetailsTemplate";
      }
    }
    model.addAttribute("garden", garden);
    return "redirect:/gardens/details?gardenId=" + id;
  }
}
