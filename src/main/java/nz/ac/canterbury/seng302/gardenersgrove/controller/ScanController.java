package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantIdentificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller responsible for handling requests related to plant identification.
 */
@Controller
public class ScanController {
    private final Logger logger = LoggerFactory.getLogger(ScanController.class);
    private final PlantIdentificationService plantIdentificationService;
    private final GardenerFormService gardenerFormService;
    private final ImageService imageService;

    /**
     * Constructs a new ScanController with the services required for sending and storing identified plants.
     *
     * @param plantIdentificationService the service for handling plant identification requests and storing identified plants
     * @param gardenerFormService        the service for retrieving information about the current gardener
     * @param imageService               the service for checking image validation
     */
    @Autowired
    public ScanController(PlantIdentificationService plantIdentificationService, GardenerFormService gardenerFormService, ImageService imageService) {
        this.plantIdentificationService = plantIdentificationService;
        this.gardenerFormService = gardenerFormService;
        this.imageService = imageService;
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


    @PostMapping("/identifyPlant")
    @ResponseBody
    public ResponseEntity<?> identifyPlant(@RequestParam("image") MultipartFile image) {
        logger.info("POST /identifyPlant");
        Optional<Gardener> gardener = getGardenerFromAuthentication();
        Map<String, String> errorResponse = new HashMap<>();

        if (image.isEmpty()) {
            errorResponse.put("error", "Please add an image to identify.");
            return ResponseEntity.badRequest().body(errorResponse);
        } else {
            Optional<String> uploadMessage = imageService.checkValidImage(image);
            if (uploadMessage.isPresent()) {
                errorResponse.put("error", uploadMessage.get());
                return ResponseEntity.badRequest().body(errorResponse);
            }
        }

        if (gardener.isPresent()) {
            try {
                IdentifiedPlant identifiedPlant = plantIdentificationService.identifyPlant(image, gardener.get());

                Map<String, Object> response = new HashMap<>();
                response.put("bestMatch", identifiedPlant.getBestMatch());
                response.put("score", identifiedPlant.getScore());
                response.put("commonNames", identifiedPlant.getCommonNames());
                response.put("gbifId", identifiedPlant.getGbifId());
                response.put("imageUrl", identifiedPlant.getImageUrl());

                return ResponseEntity.ok(response);
            } catch (Exception e) {
                if (e.getMessage().contains("Species not found")) {
                    errorResponse.put("error", "Sorry, we could not identify your image. Try with a different image.");
                } else {
                    errorResponse.put("error", e.getMessage());
                }
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } else {
            errorResponse.put("error", "User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

//    /**
//     * Handles POST requests to the /scan endpoint.
//     * Processes the uploaded image, identifies the plant, and displays the result of the identification
//     *
//     * @param image the image file uploaded by the user for plant identification
//     * @param model the model to hold attributes for rendering in the view
//     * @return the name of the template to display the identification results or errors
//     */
//    @PostMapping("/scan")
//    public String sendScanForm(@RequestParam("image") MultipartFile image, Model model) {
//        Optional<Gardener> gardener = getGardenerFromAuthentication();
//        if (gardener.isPresent()) {
//            try {
//                IdentifiedPlant identifiedPlant = plantIdentificationService.identifyPlant(image, gardener.get());
//
//                model.addAttribute("bestMatch", identifiedPlant.getBestMatch());
//                model.addAttribute("score", identifiedPlant.getScore());
//                model.addAttribute("speciesScientificNameWithoutAuthor", identifiedPlant.getSpeciesScientificNameWithoutAuthor());
//                model.addAttribute("speciesScientificNameAuthorship", identifiedPlant.getSpeciesScientificNameAuthorship());
//                model.addAttribute("speciesScientificName", identifiedPlant.getSpeciesScientificName());
//                model.addAttribute("genusScientificNameWithoutAuthor", identifiedPlant.getGenusScientificNameWithoutAuthor());
//                model.addAttribute("genusScientificNameAuthorship", identifiedPlant.getGenusScientificNameAuthorship());
//                model.addAttribute("genusScientificName", identifiedPlant.getGenusScientificName());
//                model.addAttribute("familyScientificNameWithoutAuthor", identifiedPlant.getFamilyScientificNameWithoutAuthor());
//                model.addAttribute("familyScientificNameAuthorship", identifiedPlant.getFamilyScientificNameAuthorship());
//                model.addAttribute("familyScientificName", identifiedPlant.getFamilyScientificName());
//                model.addAttribute("commonNames", identifiedPlant.getCommonNames());
//                model.addAttribute("gbifId", identifiedPlant.getGbifId());
//                model.addAttribute("powoId", identifiedPlant.getPowoId());
//                model.addAttribute("iucnId", identifiedPlant.getIucnId());
//                model.addAttribute("iucnCategory", identifiedPlant.getIucnCategory());
//                model.addAttribute("imageUrl", identifiedPlant.getImageUrl());
//
//                return "scan";
//            } catch (Exception e) {
//                model.addAttribute("error", "Error: " + e.getMessage());
//                return "scan";
//            }
//        } else {
//            model.addAttribute("error", "Error: User not authenticated");
//            return "scan";
//        }
//    }
}
