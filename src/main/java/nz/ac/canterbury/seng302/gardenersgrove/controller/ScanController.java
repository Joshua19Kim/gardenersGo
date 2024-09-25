package nz.ac.canterbury.seng302.gardenersgrove.controller;

import java.util.*;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Badge;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


/**
 * Controller responsible for handling requests related to plant identification.
 */
@Controller
public class ScanController {
    private final Logger logger = LoggerFactory.getLogger(ScanController.class);
    private final PlantIdentificationService plantIdentificationService;
    private final IdentifiedPlantService identifiedPlantService;
    private final GardenerFormService gardenerFormService;
    private final ImageService imageService;
    private  final BadgeService badgeService;
    private final Map<String, String> errorResponse;
    private final Map<String, Object> response;
    private IdentifiedPlant identifiedPlant;

    private final String errorKey = "error";

    /**
     * Constructs a new ScanController with the services required for sending and storing identified plants.
     *
     * @param plantIdentificationService the service for handling plant identification requests and storing identified plants
     * @param gardenerFormService        the service for retrieving information about the current gardener
     * @param imageService               the service for checking image validation
     */
    @Autowired
    public ScanController(PlantIdentificationService plantIdentificationService, GardenerFormService gardenerFormService, ImageService imageService, IdentifiedPlantService identifiedPlantService, BadgeService badgeService) {
        this.plantIdentificationService = plantIdentificationService;
        this.gardenerFormService = gardenerFormService;
        this.imageService = imageService;
        errorResponse = new HashMap<>();
        response = new HashMap<>();
        this.identifiedPlantService = identifiedPlantService;
        this.badgeService = badgeService;
    }

    /**
     * Retrieve an optional of a gardener using the current authentication
     *
     * @return An optional of the requested gardener
     */
    public Optional<Gardener> getGardenerFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        return gardenerFormService.findByEmail(currentUserEmail);
    }


    /**
     * Handles POST requests to the /identifyPlant endpoint.
     * Processes the uploaded image, identifies the plant, and displays the result of the identification
     *
     * @param image the image file uploaded by the user for plant identification
     * @return ResponseEntity<?> with one of the following:
     *         - ResponseEntity.ok() with a Map containing identification details if successful
     *         - ResponseEntity.badRequest() with an error message if the image is empty or invalid
     *         - ResponseEntity.badRequest() with an error message if plant identification fails
     *         - ResponseEntity.status(HttpStatus.UNAUTHORIZED) if the user is not authenticated
     */
    @PostMapping("/identifyPlant")
    @ResponseBody
    public ResponseEntity<?> identifyPlant(@RequestParam("image") MultipartFile image) {
        logger.info("POST /identifyPlant");
        Optional<Gardener> gardener = getGardenerFromAuthentication();

        if (image.isEmpty()) {
            errorResponse.put(errorKey, "Please add an image to identify.");
            return ResponseEntity.badRequest().body(errorResponse);
        } else {
            Optional<String> uploadMessage = imageService.checkValidPlantImage(image);
            if (uploadMessage.isPresent()) {
                errorResponse.put(errorKey, uploadMessage.get());
                return ResponseEntity.badRequest().body(errorResponse);
            }
        }

        if (gardener.isPresent()) {
            try {
                identifiedPlant = plantIdentificationService.identifyPlant(image, gardener.get());
                if (identifiedPlant.getScore() >= 0.3) {
                    response.put("bestMatch", identifiedPlant.getBestMatch());
                    response.put("score", identifiedPlant.getScore());
                    response.put("commonNames", identifiedPlant.getCommonNames());
                    response.put("gbifId", identifiedPlant.getGbifId());
                    response.put("imageUrl", identifiedPlant.getImageUrl());

                    return ResponseEntity.ok(response);
                } else {
                    errorResponse.put(errorKey, "Please ensure the plant is taking up most of the frame and the photo is not blurry.");
                    return ResponseEntity.badRequest().body(errorResponse);
                }


            } catch (Exception e) {
                if (e.getMessage().contains("Species not found")) {
                    errorResponse.put(errorKey, "There is no matching plant with your image. Please try with a different image of the plant.");
                } else if (e.getMessage().contains("Unsupported file type for image")) {
                    errorResponse.put(errorKey, "Image must be of type png or jpg.");
                }else {
                    errorResponse.put(errorKey, e.getMessage());
                }
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } else {
            errorResponse.put(errorKey, "User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    /**
     * Handles POST requests to the /saveIdentifiedPlant endpoint.
     * Process the data of the identified plant
     * @return ResponseEntity<?> with one of the following:
     *         - ResponseEntity.ok() with a success message if the plant data is saved successfully
     *         - ResponseEntity.badRequest() with an error message if saving the plant data fails
     *         - ResponseEntity.status(HttpStatus.UNAUTHORIZED) if the user is not authenticated
     */
    @PostMapping("/saveIdentifiedPlant")
    @ResponseBody
    public ResponseEntity<?> saveIdentifiedPlant(
            @RequestBody Map<String, String> extra
    ) {
        logger.info("POST /saveIdentifiedPlant");
        Optional<Gardener> gardener = getGardenerFromAuthentication();

        if (gardener.isPresent()) {
            try {
                String name = extra.get("name");
                String description = extra.get("description");
                String plantLatitude = extra.get("plantLatitude");
                String plantLongitude = extra.get("plantLongitude");
                if (plantLongitude != null && plantLongitude.isBlank()) {
                    plantLongitude = null;
                }
                if (plantLatitude != null && plantLatitude.isBlank()) {
                    plantLatitude = null;
                }

                String validatedPlantName = ValidityChecker.validateIdentifiedPlantName(name);
                String validatedPlantDescription = ValidityChecker.validateIdentifiedPlantDescription(description);
                boolean validLocation = ValidityChecker.validatePlantCoordinates(plantLatitude, plantLongitude);

                boolean isValid = true;

                if (!Objects.equals(name, validatedPlantName)) {
                    errorResponse.put("nameError", validatedPlantName);
                    isValid = false;
                }
                if (!Objects.equals(description, validatedPlantDescription)) {
                    errorResponse.put("descriptionError", validatedPlantDescription);
                    isValid = false;
                }
                if (!validLocation) {
                    errorResponse.put("locationError", "Invalid Location");
                    isValid = false;
                }

                if (isValid) {
                    identifiedPlant.setName(validatedPlantName);
                    boolean descriptionPresent = description != null && !validatedPlantDescription.trim().isEmpty();
                    if (descriptionPresent) {
                        identifiedPlant.setDescription(validatedPlantDescription);
                    }
                    identifiedPlant.setPlantLatitude(plantLatitude);
                    identifiedPlant.setPlantLongitude(plantLongitude);
                    response.put("message", "Plant saved successfully");
                    int originalSpeciesCount = identifiedPlantService.getSpeciesCount(gardener.get().getId());
                    IdentifiedPlant savedPlant = identifiedPlantService.saveIdentifiedPlantDetails(identifiedPlant);
                    Integer plantCount = identifiedPlantService.getCollectionPlantCount(gardener.get().getId());
                    Optional<Badge> plantBadge = badgeService.checkPlantBadgeToBeAdded(gardener.get(), plantCount);
                    if(plantBadge.isPresent()) {
                        response.put("plantBadge", plantBadge.get().getId());
                    } else {
                        response.remove("plantBadge");
                    }
                    response.put("savedPlant", savedPlant.getId());
                    int speciesCount = identifiedPlantService.getSpeciesCount(gardener.get().getId());
                    if(speciesCount != originalSpeciesCount) {
                        Optional<Badge> speciesBadge = badgeService.checkSpeciesBadgeToBeAdded(gardener.get(), speciesCount);
                        speciesBadge.ifPresent(badge -> response.put("speciesBadge", speciesBadge.get().getId()));
                    } else {
                        response.remove("speciesBadge");
                    }
                    return ResponseEntity.ok(response);
                }
                errorResponse.put("message", "Invalid Field");
                return ResponseEntity.badRequest().body(errorResponse);

            } catch (Exception e) {
                errorResponse.put(errorKey, "Failed to save the identified plant: " + e.getMessage());
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } else {
            errorResponse.put(errorKey, "User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

}
