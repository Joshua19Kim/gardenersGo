package nz.ac.canterbury.seng302.gardenersgrove.controller;

import static java.lang.Long.parseLong;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller class for handling HTTP requests for the Collections page
 */
@Controller
public class CollectionsController {
    private final IdentifiedPlantService identifiedPlantService;
    Logger logger = LoggerFactory.getLogger(CollectionsController.class);

    private final int pageSize;

    private final ImageService imageService;

    private final GardenService gardenService;

    private final GardenerFormService gardenerFormService;

    private final BadgeService badgeService;

    private final RequestService requestService;

    private final LocationService locationService;
    private Gardener gardener;

    private static final String PAGINATION_MESSAGE_ATTRIBUTE = "paginationMessage";
    private static final String ERROR_OCCURRED_ATTRIBUTE = "errorOccurred";
    private static final String SHOW_MODAL_ATTRIBUTE = "showModal";
    private static final String SUCCESS_MESSAGE_ATTRIBUTE = "successMessage";
    private static final String ERROR_KEY = "error";
    private static final String REGION_BADGE_NAME = "regionBadge";
    private static final String BADGE_COUNT_NAME = "badgeCount";
    private static final String SPECIES_NAME = "speciesName";

    private final PlantIdentificationService plantIdentificationService;

    private final Map<String, String> errorResponse;
    private final Map<String, Object> response;



    /**
     * Constructor to create a collections controller object
     * @param imageService service class used to save images
     * @param gardenService service class used to interact with the garden database
     * @param gardenerFormService service class used to interact with the user (gardener) database
     * @param identifiedPlantService service class used to interact with the identified plants database
     * @param requestService service class used to get the request URI
     * @param plantIdentificationService service used to identify plants
     * @param badgeService service class used to interact with the badge database
     */
    public CollectionsController(ImageService imageService, GardenService gardenService,
                                 GardenerFormService gardenerFormService, IdentifiedPlantService identifiedPlantService,
                                 RequestService requestService,
                                 PlantIdentificationService plantIdentificationService, BadgeService badgeService, LocationService locationService) {
        this.imageService = imageService;
        this.gardenService = gardenService;
        this.gardenerFormService = gardenerFormService;
        this.identifiedPlantService = identifiedPlantService;
        this.plantIdentificationService = plantIdentificationService;
        this.locationService = locationService;
        errorResponse = new HashMap<>();
        response = new HashMap<>();
        this.requestService = requestService;
        pageSize = 11;
        this.badgeService = badgeService;
    }

    /**
     * Retrieve an optional of a gardener using the current authentication. We will always have to
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
     * Handles GET requests for /myCollection stub and returns the template for
     * my collections page
     *
     * @param pageNoString string representation of the page number used for pagination
     * @param plantBadgeId this is the id of a plant badge that the user has just earned
     * @param speciesBadgeId this is the id of a species badge that the user has just earned
     * @param savedPlantId this is the id of the plant that you save so that a notification appears
     * @param model        used for passing attributes to the view
     * @return myCollectionTemplate
     */
    @GetMapping("/myCollection")
    public String getMyCollection(
            @RequestParam(name="pageNo", defaultValue = "0") String pageNoString,
            @RequestParam(name="plantBadgeId", required = false) String plantBadgeId,
            @RequestParam(name="speciesBadgeId", required = false) String speciesBadgeId,
            @RequestParam(name="regionBadgeId", required = false) String regionBadgeId,
            @RequestParam(name="savedPlant", defaultValue = "") String savedPlantId,
            Model model) {

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        int pageNo = ValidityChecker.validatePageNumber(pageNoString);
        Page<IdentifiedPlantSpeciesImpl> speciesList = identifiedPlantService.getGardenerPlantSpeciesPaginated(pageNo, pageSize, gardener.getId());
        logger.info("GET /myCollection");
        model.addAttribute("speciesList", speciesList);
        int totalPages = speciesList.getTotalPages();
        if (totalPages > 0) {
            int lowerBound = Math.max(pageNo - 1, 1);
            int upperBound = Math.min(pageNo + 3, totalPages);
            List<Integer> pageNumbers = IntStream.rangeClosed(lowerBound, upperBound)
                    .boxed()
                    .toList();
            model.addAttribute("pageNumbers", pageNumbers);

            long totalItems = speciesList.getTotalElements();
            int startIndex = pageSize * pageNo + 1;
            long endIndex = Math.min((long) pageSize * (pageNo + 1), totalItems);
            String paginationMessage = "Showing results " + startIndex + " to " + endIndex + " of " + totalItems;
            model.addAttribute(PAGINATION_MESSAGE_ATTRIBUTE, paginationMessage);
        } else {
            String paginationMessage = "Showing results 0 to 0 of 0";
            model.addAttribute(PAGINATION_MESSAGE_ATTRIBUTE, paginationMessage);
        }


        // For Autocomplete
        List<String> plantScientificNames = plantIdentificationService.getAllSpeciesScientificNames(gardener);
        List<String> plantNames = plantIdentificationService.getAllPlantNames(gardener);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String plantNamesJson = objectMapper.writeValueAsString(plantNames);
            String plantScientificNamesJson = objectMapper.writeValueAsString(plantScientificNames);
            model.addAttribute("plantNamesJson", plantNamesJson);
            model.addAttribute("plantScientificNamesJson", plantScientificNamesJson);
        } catch (JsonProcessingException e) {
            logger.error("Error converting lists to JSON", e);
        }

        if(!model.containsAttribute(ERROR_OCCURRED_ATTRIBUTE)) {
            model.addAttribute(ERROR_OCCURRED_ATTRIBUTE, false);
        }
        if (!model.containsAttribute(SHOW_MODAL_ATTRIBUTE)) {
            model.addAttribute(SHOW_MODAL_ATTRIBUTE, false);
        }
        int badgeCount = 0;
        badgeCount = addBadgeToModel(plantBadgeId, "plantBadge", gardener, badgeCount, model);
        badgeCount = addBadgeToModel(speciesBadgeId, "speciesBadge", gardener, badgeCount, model);
        badgeCount = addBadgeToModel(regionBadgeId, REGION_BADGE_NAME, gardener, badgeCount, model);
        if(!model.containsAttribute(BADGE_COUNT_NAME)) {
            model.addAttribute(BADGE_COUNT_NAME, badgeCount);
        }

        if (!savedPlantId.isEmpty()) {
            IdentifiedPlant savedPlant = identifiedPlantService.getCollectionPlantById(Long.parseLong(savedPlantId));
            if (savedPlant != null && savedPlant.getGardener().equals(gardener)) {
                if (savedPlant.getSpeciesScientificNameWithoutAuthor().isEmpty()) {
                    model.addAttribute(SUCCESS_MESSAGE_ATTRIBUTE, savedPlant.getName() + " has been added to collection");
                } else {
                    model.addAttribute(SUCCESS_MESSAGE_ATTRIBUTE, savedPlant.getName() + " has been added to collection: " + savedPlant.getSpeciesScientificNameWithoutAuthor());
                }
            }
        }

        return "myCollectionTemplate";
    }


    /**
     * Displays all the plants that have been added to the collection that are a part of the specified species
     *
     * @param speciesName  the name of the species
     * @param pageNoString the page number
     * @param model        the model
     * @return the collection details template
     */
    @GetMapping("/collectionDetails")
    public String getSpeciesDetails(
            @RequestParam(name = SPECIES_NAME, required = false) String speciesName,
            @RequestParam(name = "pageNo", defaultValue = "0") String pageNoString,
            Model model) {

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);
        int pageNo = ValidityChecker.validatePageNumber(pageNoString);
        if(speciesName == null) {
            speciesName = (String) model.getAttribute(SPECIES_NAME);
        }
        Page<IdentifiedPlant> collectionsList = identifiedPlantService.getGardenerPlantsBySpeciesPaginated(pageNo, pageSize, gardener.getId(), speciesName);
        model.addAttribute("collectionsList", collectionsList);
        model.addAttribute(SPECIES_NAME, speciesName);

        int totalPages = collectionsList.getTotalPages();
        if (totalPages > 0) {
            int lowerBound = Math.max(pageNo - 1, 1);
            int upperBound = Math.min(pageNo + 3, totalPages);
            List<Integer> pageNumbers = IntStream.rangeClosed(lowerBound, upperBound)
                    .boxed()
                    .toList();
            model.addAttribute("pageNumbers", pageNumbers);

            long totalItems = collectionsList.getTotalElements();
            int startIndex = pageSize * pageNo + 1;
            long endIndex = Math.min((long) pageSize * (pageNo + 1), totalItems);
            String paginationMessage = "Showing results " + startIndex + " to " + endIndex + " of " + totalItems;
            model.addAttribute(PAGINATION_MESSAGE_ATTRIBUTE, paginationMessage);
        } else {
            String paginationMessage = "Showing results 0 to 0 of 0";
            model.addAttribute(PAGINATION_MESSAGE_ATTRIBUTE, paginationMessage);
        }

        // Add gardens to the model for the navbar
        List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());
        model.addAttribute("gardens", gardens);

        return "collectionDetailsTemplate";
    }


    /**
     * This post method is used when the user manually adds a plant to their collection. It validates all the user inputs
     * and adds error messages where appropriate.
     *
     * @param plantName          the name of the plant
     * @param description        the description of the plant
     * @param scientificName     the scientific name of the plant
     * @param uploadedDate       the date uploaded
     * @param isDateInvalid      whether HTML picked up a date error or not
     * @param plantImage         the image uploaded for the plant
     * @param redirectAttributes used to add flash attributes for redirection.
     * @return returns my collection template
     */
    @PostMapping("/myCollection")
    public String addPlantToCollection(
            @RequestParam(name = "plantName") String plantName,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "scientificName", required = false) String scientificName,
            @RequestParam(name = "uploadedDate", required = false) LocalDate uploadedDate,
            @RequestParam(name = "isDateInvalid", required = false) boolean isDateInvalid,
            @RequestParam("plantImage") MultipartFile plantImage,
            @RequestParam(name ="manualPlantLat",required = false) String manualPlantLat,
            @RequestParam(name ="manualPlantLon",required = false) String manualPlantLon,
            @RequestParam(name ="location", required = false ) String location,
            @RequestParam(name= "manualAddLocationToggle", required = false) boolean manualAddLocationToggle,
            RedirectAttributes redirectAttributes
    ) throws IOException, InterruptedException {
        logger.info("/myCollection/addNewPlantToMyCollection");
        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        if (manualPlantLat != null && manualPlantLat.isBlank()) {
            manualPlantLat = null;
        }
        if (manualPlantLon != null && manualPlantLon.isBlank()) {
            manualPlantLon = null;
        }

        boolean isValid = validateManuallyAddedPlantDetails(plantName, scientificName, description, isDateInvalid, redirectAttributes);
        boolean validLocation = ValidityChecker.validatePlantCoordinates(manualPlantLat,manualPlantLon);

        if (!plantImage.isEmpty()) {
            Optional<String> uploadMessage = imageService.checkValidImage(plantImage);
            if (uploadMessage.isPresent()) {
                redirectAttributes.addFlashAttribute("uploadError", uploadMessage.get());
                isValid = false;
            }
        }

        if (!validLocation) {
            redirectAttributes.addFlashAttribute("locationError", "Invalid Location");
            isValid = false;
        }
        if (manualPlantLon == null && manualPlantLat == null) {
            manualAddLocationToggle = false;
        }

        if (isValid) {
            IdentifiedPlant identifiedPlant = new IdentifiedPlant(plantName, gardener);
            if (manualPlantLat != null && manualPlantLon != null && !manualPlantLat.isEmpty() && !manualPlantLon.isEmpty()) {
                String region = locationService.sendReverseGeocodingRequest(manualPlantLat, manualPlantLon);
                identifiedPlant.setRegion(region);
            }
            identifiedPlant.setPlantLatitude(manualPlantLat);
            identifiedPlant.setPlantLongitude(manualPlantLon);

            identifiedPlant = identifiedPlantService.createManuallyAddedPlant(identifiedPlant, description, scientificName, uploadedDate);

            int originalSpeciesCount = identifiedPlantService.getSpeciesCount(gardener.getId());
            int originalRegionCount = identifiedPlantService.getRegionCount(gardener.getId());

            if (plantImage.isEmpty()) {
                identifiedPlant.setUploadedImage("/images/placeholder.jpg");
                identifiedPlantService.saveIdentifiedPlantDetails(identifiedPlant);
            } else {
                identifiedPlantService.saveIdentifiedPlantDetails(identifiedPlant);
                imageService.saveCollectionPlantImage(plantImage, identifiedPlant);
            }
            int badgeCount = 0;
            Integer plantCount = identifiedPlantService.getCollectionPlantCount(gardener.getId());
            Optional<Badge> plantBadge = badgeService.checkPlantBadgeToBeAdded(gardener, plantCount);
            if (plantBadge.isPresent()) {
                redirectAttributes.addFlashAttribute("plantBadge", plantBadge.get());
                badgeCount += 1;
            }
            if (scientificName == null || scientificName.isEmpty()) {
                redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE_ATTRIBUTE, plantName + " has been added to collection");
            } else {
                redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE_ATTRIBUTE, plantName + " has been added to collection: " + scientificName);
            }
            int speciesCount = identifiedPlantService.getSpeciesCount(gardener.getId());
            if(speciesCount != originalSpeciesCount) {
                Optional<Badge> speciesBadge = badgeService.checkSpeciesBadgeToBeAdded(gardener, speciesCount);
                if(speciesBadge.isPresent()) {
                    redirectAttributes.addFlashAttribute("speciesBadge", speciesBadge.get());
                    badgeCount += 1;
                }
            }
            int regionCount = identifiedPlantService.getRegionCount(gardener.getId());
            if(regionCount != originalRegionCount) {
                Optional<Badge> regionBadge = badgeService.checkRegionBadgeToBeAdded(gardener, regionCount);
                if(regionBadge.isPresent()) {
                    redirectAttributes.addFlashAttribute(REGION_BADGE_NAME, regionBadge.get());
                    badgeCount += 1;
                }
            }

            redirectAttributes.addFlashAttribute(BADGE_COUNT_NAME, badgeCount);

            return "redirect:/myCollection";
        } else {
            redirectAttributes.addFlashAttribute("plantName", plantName);
            redirectAttributes.addFlashAttribute("description", description);
            redirectAttributes.addFlashAttribute("scientificName", scientificName);
            redirectAttributes.addFlashAttribute("uploadedDate", uploadedDate);
            redirectAttributes.addFlashAttribute("manualPlantLat", manualPlantLat);
            redirectAttributes.addFlashAttribute("manualPlantLon", manualPlantLon);
            redirectAttributes.addFlashAttribute("location", location);
            redirectAttributes.addFlashAttribute("manualAddLocationToggle", manualAddLocationToggle);
            redirectAttributes.addFlashAttribute(ERROR_OCCURRED_ATTRIBUTE, true);
            redirectAttributes.addFlashAttribute(SHOW_MODAL_ATTRIBUTE, true);

            return "redirect:/myCollection";
        }
    }

    @PostMapping("/myCollection/autoPopulate")
    @ResponseBody
    public ResponseEntity<?> autoPopulateDetails(
            @RequestBody Map<String, String> selectedInfo) {
        logger.info("POST /myCollection/autoPopulate");
        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        try {
            String name = selectedInfo.get("name");
            boolean isPlantName = Boolean.parseBoolean(selectedInfo.get("isPlantName"));
            boolean isSpecieScientificName = Boolean.parseBoolean(selectedInfo.get("isSpecieScientificName"));

            List<Map<String, String>> plantDetailsList;
            if (isPlantName) {
                plantDetailsList = plantIdentificationService.getPlantDetailsWithPlantNames(name);
            } else if (isSpecieScientificName) {
                plantDetailsList = plantIdentificationService.getPlantDetailsWithSpeciesScientificName(name);
            } else {
                plantDetailsList = new ArrayList<>();
            }

            if (plantDetailsList.isEmpty()) {
                errorResponse.put(ERROR_KEY, "Plant not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            ObjectMapper mapper = new ObjectMapper();
            String jsonResult = mapper.writeValueAsString(plantDetailsList);

            return ResponseEntity.ok(jsonResult);
        } catch (Exception e) {
            errorResponse.put(ERROR_KEY, "Failed to save the identified plant: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     *
     * @param plantIdString string version of id of plant being edited
     * @param model         used for passing attributes to the view
     * @return edit form or redirect back to collection
     */
    @GetMapping("/collectionDetails/edit")
    public String editIdentifiedPlant(
            @RequestParam(name = "plantId") String plantIdString,
            Model model, HttpServletRequest request) throws IOException, InterruptedException {

        logger.info("GET /collectionDetails/edit");

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        long plantId = parseLong(plantIdString, 10);
        IdentifiedPlant plant = identifiedPlantService.getCollectionPlantById(plantId);

        if (plant != null) {
            model.addAttribute("requestURI", requestService.getRequestURI(request));
            model.addAttribute("plant", plant);
            if (plant.getPlantLatitude() != null && plant.getPlantLongitude() != null) {
                String savedLocation = locationService.getLocationfromLatLong(plant.getPlantLatitude(), plant.getPlantLongitude());
                model.addAttribute("savedLocation", savedLocation);
            }

            // need to add to model so that the navbar can populate the dropdown
            List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());
            model.addAttribute("gardens", gardens);

            return "editIdentifiedPlantForm";
        } else {
            return "redirect:/collectionDetails";
        }
    }

    /**
     * Handles the submission of the edit plant form.
     *
     * @param name          The updated name of the plant.
     * @param description   The updated description of the plant.
     * @param plantIdString The string ID of the plant being edited.
     * @param model         The model for passing data to the view.
     * @return The template for the edit plant form or redirects to the garden details page.
     */
    @PostMapping("collectionDetails/edit")
    public String submitEditIdentifiedPlantForm(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "plantId") String plantIdString,
            @RequestParam(name ="manualPlantLat",required = false) String manualPlantLat,
            @RequestParam(name ="manualPlantLon",required = false) String manualPlantLon,
            @RequestParam(name ="location", required = false ) String location,
            @RequestParam(name= "manualAddLocationToggle", required = false) boolean manualAddLocationToggle,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            Model model) throws IOException, InterruptedException {

        logger.info("POST /collectionDetails/edit");

        long plantId = parseLong(plantIdString, 10);
        IdentifiedPlant plantOptional = identifiedPlantService.getCollectionPlantById(plantId);

        if (manualPlantLat != null && manualPlantLat.isBlank()) {
            manualPlantLat = null;
        }
        if (manualPlantLon != null && manualPlantLon.isBlank()) {
            manualPlantLon = null;
        }

        String validatedPlantName = ValidityChecker.validateIdentifiedPlantName(name);
        String validatedPlantDescription = ValidityChecker.validateIdentifiedPlantDescription(description);
        boolean validLocation = ValidityChecker.validatePlantCoordinates(manualPlantLat,manualPlantLon);



        boolean isValid = true;

        if (!Objects.equals(name, validatedPlantName)) {
            model.addAttribute("nameError", validatedPlantName);
            isValid = false;
        }
        if (!Objects.equals(description, validatedPlantDescription)) {
            model.addAttribute("descriptionError", validatedPlantDescription);
            isValid = false;
        }
        if (!validLocation) {
            model.addAttribute("locationError", "Invalid Location");
            isValid = false;
        }
        if (manualPlantLon == null && manualPlantLat == null) {
            manualAddLocationToggle = false;
        }


        if (isValid && plantOptional != null) {
            IdentifiedPlant plant = plantOptional;
            plant.setPlantLatitude(manualPlantLat);
            plant.setPlantLongitude(manualPlantLon);

            int originalRegionCount = identifiedPlantService.getRegionCount(gardener.getId());

            if (manualPlantLat == null && manualPlantLon == null) {
                plant.setRegion(null);
            } else {
                String region = locationService.sendReverseGeocodingRequest(manualPlantLat, manualPlantLon);
                plant .setRegion(region);
            }

            plant.setName(validatedPlantName);
            boolean descriptionPresent = !Objects.equals(validatedPlantDescription.trim(), "");

            if (descriptionPresent) {
                plant.setDescription(validatedPlantDescription);
            } else {
                plant.setDescription(null);
            }
            identifiedPlantService.saveIdentifiedPlantDetails(plant);

            int badgeCount = 0;
            int regionCount = identifiedPlantService.getRegionCount(gardener.getId());
            if(regionCount < originalRegionCount) {
                badgeService.checkIfBadgeShouldBeRemoved(originalRegionCount, regionCount, gardener);
            } else if(regionCount != originalRegionCount) {
                Optional<Badge> regionBadge = badgeService.checkRegionBadgeToBeAdded(gardener, regionCount);
                if(regionBadge.isPresent()) {
                    redirectAttributes.addFlashAttribute(REGION_BADGE_NAME, regionBadge.get());
                    badgeCount += 1;
                }
            }


            redirectAttributes.addFlashAttribute(BADGE_COUNT_NAME, badgeCount);
            redirectAttributes.addFlashAttribute(SPECIES_NAME, plant.getSpeciesScientificNameWithoutAuthor());


            return "redirect:/collectionDetails";
        } else {
            Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
            gardenerOptional.ifPresent(value -> gardener = value);
            model.addAttribute("requestURI", requestService.getRequestURI(request));
            model.addAttribute("name", name);
            model.addAttribute("plant", plantOptional);
            model.addAttribute("description", description);
            model.addAttribute("manualPlantLat", manualPlantLat);
            model.addAttribute("manualPlantLon", manualPlantLon);
            model.addAttribute("location", location);
            model.addAttribute("manualAddLocationToggle", manualAddLocationToggle);

            return "editIdentifiedPlantForm";
        }
    }

    /**
     * This validates all the details of a plant that is manually added to the collection
     * @param plantName the plant name
     * @param scientificName the scientific name (species)
     * @param description the description
     * @param isDateInvalid indicates whether the date is valid or not from HTML
     * @param redirectAttributes used to add flash attributes when the page is redirected
     * @return a boolean value indicating whether the plant is added or not
     */
    public boolean validateManuallyAddedPlantDetails(String plantName, String scientificName, String description, boolean isDateInvalid, RedirectAttributes redirectAttributes) {
        String validatedPlantName = ValidityChecker.validatePlantName(plantName);
        String validatedScientificName = ValidityChecker.validateScientificPlantName(scientificName);
        String validatedPlantDescription = ValidityChecker.validatePlantDescription(description);

        boolean isValid = true;

        if (isDateInvalid) {
            String dateError = "Date is not in valid format, DD/MM/YYYY";
            redirectAttributes.addFlashAttribute("dateError", dateError);
            isValid = false;
        }

        if (!Objects.equals(plantName, validatedPlantName)) {
            redirectAttributes.addFlashAttribute("plantNameError", validatedPlantName);
            isValid = false;
        }
        if (!Objects.equals(scientificName, validatedScientificName)) {
            redirectAttributes.addFlashAttribute("scientificNameError", validatedScientificName);
            isValid = false;
        }
        if (!Objects.equals(description, validatedPlantDescription)) {
            redirectAttributes.addFlashAttribute("descriptionError", validatedPlantDescription);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Adds the badge to the specific model if it exists
     * @param badgeId the badge id
     * @param badgeName the badge name
     * @param gardener the gardener
     * @param badgeCount the badge count
     * @param model the model
     * @return the badge count
     */
    public int addBadgeToModel(String badgeId, String badgeName,  Gardener gardener, int badgeCount, Model model) {
        if(badgeId != null && !badgeId.isEmpty()) {
            try {
                long badgeIdLong = parseLong(badgeId, 10);
                Optional<Badge> badge = badgeService.getMyBadgeById(badgeIdLong, gardener.getId());
                if(badge.isPresent()) {
                    model.addAttribute(badgeName, badge.get());
                    badgeCount += 1;
                }

            } catch (Exception e) {
                logger.info(e.getMessage());
            }

        }
        return badgeCount;
    }

}
