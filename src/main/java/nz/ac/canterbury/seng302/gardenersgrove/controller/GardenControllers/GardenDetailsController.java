package nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.NotificationUtil;
import nz.ac.canterbury.seng302.gardenersgrove.util.TagValidation;
import nz.ac.canterbury.seng302.gardenersgrove.util.WordFilter;
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Long.parseLong;

/** Controller class responsible for handling garden-details-related HTTP requests. */
@Controller
public class GardenDetailsController {
    Logger logger = LoggerFactory.getLogger(GardenDetailsController.class);
    private final GardenService gardenService;
    private final GardenerFormService gardenerFormService;
    private final RelationshipService relationshipService;
    private final RequestService requestService;
    private final TagService tagService;
    private Gardener gardener;
    private final WeatherService weatherService;
    private final LocationService locationService;


    /**
     * Constructor used to create a new instance of the GardenDetailsController. Autowires a
     * various service objects
     *
     * @param gardenService       the garden service used to interact with the database
     * @param gardenerFormService the gardener form service used to interact with the database
     * @param relationshipService the relationship service used to manage gardener relationships
     * @param requestService      the request service used to manage HTTP requests
     * @param weatherService      the weather service used to retrieve weather information
     * @param tagService          the tag service used to manage garden tags
     * @param locationService     the location service used to retrieve location from API
     */
    @Autowired
    public GardenDetailsController(
            GardenService gardenService,
            GardenerFormService gardenerFormService,
            RelationshipService relationshipService,
            RequestService requestService,
            WeatherService weatherService,
            TagService tagService, LocationService locationService) {
        this.gardenService = gardenService;
        this.gardenerFormService = gardenerFormService;
        this.relationshipService = relationshipService;
        this.requestService = requestService;
        this.weatherService = weatherService;
        this.tagService = tagService;
        this.locationService = locationService;
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
           @RequestParam(name = "tagValid", required = false) String tagValid,
           Model model,
           HttpServletRequest request)
           throws IOException, URISyntaxException, InterruptedException {
       logger.info("GET /gardens/details");

       Optional<Gardener> currentUserOptional = getGardenerFromAuthentication();
       List<Garden> gardens = new ArrayList<>();
       if (currentUserOptional.isPresent()) {
           gardener = currentUserOptional.get();
           gardens = gardenService.getGardensByGardenerId(gardener.getId());
       }

       model.addAttribute("gardens", gardens);

       if (gardenId == null) {
           return "redirect:/gardens";
       }
       model.addAttribute("gardenId", gardenId);
       Optional<Garden> garden = gardenService.getGarden(parseLong(gardenId));
       if (garden.isPresent()) {
           model.addAttribute("requestURI", requestService.getRequestURI(request));
           model.addAttribute("garden", garden.get());
           model.addAttribute("tags", tagService.getTags(parseLong(gardenId)));
           model.addAttribute("allTags", tagService.getUniqueTagNames(parseLong(gardenId)));
           if (uploadError != null) {
               model.addAttribute("uploadError", uploadError);
               model.addAttribute("errorId", errorId);
           }
           if (tagValid != null) {
               model.addAttribute("tagValid", tagValid);
           }
           if (Objects.equals(garden.get().getGardener().getId(), currentUserOptional.get().getId())) {
               HttpResponse<String> location = locationService.sendRequest(garden.get().getCity() + ", " + garden.get().getCountry());
               Weather currentWeather = null;
               PrevWeather prevWeathers = null;
               logger.info("LOCATION BODY: " + location.body());

               if (! location.body().contains("error")) {
                   currentWeather = weatherService.getWeather(garden.get().getCity() + ", " + garden.get().getCountry());
                   prevWeathers = weatherService.getPrevWeather(garden.get().getCity() + ", " + garden.get().getCountry());
               }
               if (currentWeather != null && prevWeathers != null) {
                   model.addAttribute("date", currentWeather.getDate());
                   model.addAttribute("temperature", currentWeather.getTemperature());
                   model.addAttribute("weatherImage", currentWeather.getWeatherImage());
                   model.addAttribute("weatherDescription", currentWeather.getWeatherDescription());
                   model.addAttribute("humidity", currentWeather.getHumidity());
                   model.addAttribute("forecastDates", currentWeather.getForecastDates());
                   model.addAttribute("forecastMinTemperature", currentWeather.getForecastMinTemperatures());
                   model.addAttribute("forecastMaxTemperature", currentWeather.getForecastMaxTemperatures());
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
               Gardener gardenOwner = garden.get().getGardener();
               Boolean isFriend = relationshipService
                       .getCurrentUserRelationships(gardenOwner.getId())
                       .contains(currentUserOptional.get());
               if (isFriend) {
                   model.addAttribute("gardener", garden.get().getGardener());
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
     * Posts a form response with a new Garden
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


    /**
     * Dismisses a notification for a garden and sets the last notified date
     *
     * @param gardenId the ID of the garden related to the notification
     * @return a redirect string to the garden details page
     */
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
            Model model) throws IOException, URISyntaxException, InterruptedException {

        logger.info("POST /addTag");
        tag = tag.strip();
        TagValidation tagValidation = new TagValidation(tagService);
        Optional<Garden> gardenOptional = gardenService.getGarden(id);
        if (gardenOptional.isEmpty()) {
            return "redirect:/gardens";
        }
        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        List<Garden> gardens = new ArrayList<>();
        if (gardenerOptional.isPresent()) {
            gardener = gardenerOptional.get();
            gardens = gardenService.getGardensByGardenerId(gardenerOptional.get().getId());
        }
        model.addAttribute("gardens", gardens);
        Garden garden = gardenOptional.get();
        Optional<String> validTagError = tagValidation.validateTag(tag);
        Optional<String> tagInUse = tagValidation.checkTagInUse(tag, garden);
        HttpResponse<String> location = locationService.sendRequest(garden.getCity() + ", " + garden.getCountry());
        Weather currentWeather = null;
        PrevWeather prevWeathers = null;
        if (location != null) {
            currentWeather = weatherService.getWeather(garden.getCity() + ", " + garden.getCountry());
            prevWeathers = weatherService.getPrevWeather(garden.getCity() + ", " + garden.getCountry());
        }
        if (currentWeather != null && prevWeathers != null) {
            model.addAttribute("date", currentWeather.getDate());
            model.addAttribute("temperature", currentWeather.getTemperature());
            model.addAttribute("weatherImage", currentWeather.getWeatherImage());
            model.addAttribute("weatherDescription", currentWeather.getWeatherDescription());
            model.addAttribute("humidity", currentWeather.getHumidity());
            model.addAttribute("forecastDates", currentWeather.getForecastDates());
            model.addAttribute("forecastMinTemperature", currentWeather.getForecastMinTemperatures());
            model.addAttribute("forecastMaxTemperature", currentWeather.getForecastMaxTemperatures());
            model.addAttribute("forecastWeatherImage", currentWeather.getForecastImages());
            model.addAttribute(
                    "forecastWeatherDescription", currentWeather.getForecastDescriptions());
            model.addAttribute("forecastHumidities", currentWeather.getForecastHumidities());
            LocalDate currentDate = LocalDate.now();
            LocalDate lastNotifiedDate = garden.getLastNotified();
            if (lastNotifiedDate != null && lastNotifiedDate.equals(currentDate)) {
                model.addAttribute("wateringTip", null);
            }  else {
                if (lastNotifiedDate != null) {
                    gardenService.updateLastNotifiedbyId(id, null);
                }
                String wateringTip = NotificationUtil.generateWateringTip(currentWeather, prevWeathers);
                model.addAttribute("wateringTip", wateringTip);
            }


        }
        if (validTagError.isPresent()) {
            model.addAttribute("tagValid", validTagError.get());
            model.addAttribute("tag", tag);
            model.addAttribute("allTags", tagService.getUniqueTagNames(id));
            model.addAttribute("tags", tagService.getTags(garden.getId()));
            model.addAttribute("garden", garden);
            return "gardenDetailsTemplate";
        }

        if (tagInUse.isEmpty()) {
            if (!WordFilter.doesContainBadWords(tag)) {
                Tag newTag = new Tag(tag, garden);
                tagService.addTag(newTag);
                logger.info("Tag '{}' passes moderation checks", tag);
            } else {
                String warningMessage = tagService.addBadWordCount(gardener);
                gardenerFormService.addGardener(gardener);
                model.addAttribute("garden", garden);
                model.addAttribute("tag", tag);
                model.addAttribute("allTags", tagService.getUniqueTagNames(id));
                model.addAttribute("tags", tagService.getTags(garden.getId()));
                model.addAttribute("tagValid", warningMessage);
                return "gardenDetailsTemplate";
            }
        }
        model.addAttribute("garden", garden);
        return "redirect:/gardens/details?gardenId=" + id;
    }
}
