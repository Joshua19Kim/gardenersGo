package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantIdentificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ScanController {
    private final PlantIdentificationService plantIdentificationService;

    @Autowired
    public ScanController(PlantIdentificationService plantIdentificationService) {
        this.plantIdentificationService = plantIdentificationService;
    }

    @GetMapping("/scan")
    public String getScanForm() {
        return "scan";
    }

    @PostMapping("/scan")
    public String sendScanForm(@RequestParam("image") MultipartFile image, Model model) {
        try {
            IdentifiedPlant response = plantIdentificationService.identifyPlant(image);
            model.addAttribute("response", response);
            return "scan";
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
            return "scan";
        }
    }
}
