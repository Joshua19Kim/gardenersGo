package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantIdentificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Controller responsible for handling requests related to plant identification.
 */
@Controller
public class ScanController {
    private final PlantIdentificationService plantIdentificationService;
    private final GardenerFormService gardenerFormService;

    /**
     * Constructs a new ScanController with the services required for sending and storing identified plants.
     *
     * @param plantIdentificationService the service for handling plant identification requests and storing identified plants
     * @param gardenerFormService        the service for retrieving information about the current gardener
     */
    @Autowired
    public ScanController(PlantIdentificationService plantIdentificationService, GardenerFormService gardenerFormService) {
        this.plantIdentificationService = plantIdentificationService;
        this.gardenerFormService = gardenerFormService;
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
     * Handles GET requests to the /scan endpoint.
     * Displays the scan form where users can upload an image for plant identification.
     *
     * @return the name of the template containing the form for uploading plant images
     */
    @GetMapping("/scan")
    public String getScanForm() {
        return "scan";
    }

    /**
     * Handles POST requests to the /scan endpoint.
     * Processes the uploaded image, identifies the plant, and displays the result of the identification
     *
     * @param image the image file uploaded by the user for plant identification
     * @param model the model to hold attributes for rendering in the view
     * @return the name of the template to display the identification results or errors
     */
    @PostMapping("/scan")
    public String sendScanForm(@RequestParam("image") MultipartFile image, Model model) {
        Optional<Gardener> gardener = getGardenerFromAuthentication();
        if (gardener.isPresent()) {
            try {
                IdentifiedPlant identifiedPlant = plantIdentificationService.identifyPlant(image, gardener.get());

                model.addAttribute("bestMatch", identifiedPlant.getBestMatch());
                model.addAttribute("score", identifiedPlant.getScore());
                model.addAttribute("speciesScientificNameWithoutAuthor", identifiedPlant.getSpeciesScientificNameWithoutAuthor());
                model.addAttribute("speciesScientificNameAuthorship", identifiedPlant.getSpeciesScientificNameAuthorship());
                model.addAttribute("speciesScientificName", identifiedPlant.getSpeciesScientificName());
                model.addAttribute("genusScientificNameWithoutAuthor", identifiedPlant.getGenusScientificNameWithoutAuthor());
                model.addAttribute("genusScientificNameAuthorship", identifiedPlant.getGenusScientificNameAuthorship());
                model.addAttribute("genusScientificName", identifiedPlant.getGenusScientificName());
                model.addAttribute("familyScientificNameWithoutAuthor", identifiedPlant.getFamilyScientificNameWithoutAuthor());
                model.addAttribute("familyScientificNameAuthorship", identifiedPlant.getFamilyScientificNameAuthorship());
                model.addAttribute("familyScientificName", identifiedPlant.getFamilyScientificName());
                model.addAttribute("commonNames", identifiedPlant.getCommonNames());
                model.addAttribute("gbifId", identifiedPlant.getGbifId());
                model.addAttribute("powoId", identifiedPlant.getPowoId());
                model.addAttribute("iucnId", identifiedPlant.getIucnId());
                model.addAttribute("iucnCategory", identifiedPlant.getIucnCategory());
                model.addAttribute("imageUrl", identifiedPlant.getImageUrl());

                return "scan";
            } catch (Exception e) {
                model.addAttribute("error", "Error: " + e.getMessage());
                return "scan";
            }
        } else {
            model.addAttribute("error", "Error: User not authenticated");
            return "scan";
        }
    }
}
