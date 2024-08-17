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

@Controller
public class ScanController {
    private final PlantIdentificationService plantIdentificationService;
    private final GardenerFormService gardenerFormService;

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

    @GetMapping("/scan")
    public String getScanForm() {
        return "scan";
    }

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
