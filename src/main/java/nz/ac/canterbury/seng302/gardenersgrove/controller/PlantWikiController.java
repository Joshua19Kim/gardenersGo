package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class PlantWikiController {

    Logger logger = LoggerFactory.getLogger(PlantWikiController.class);

    private final PlantWikiService plantWikiService;

    private final GardenerFormService gardenerFormService;

    private final GardenService gardenService;

    private final ImageDownloadService imageDownloadService;


    private Gardener gardener;




    @Autowired
    public PlantWikiController(PlantWikiService plantWikiService, GardenerFormService gardenerFormService, GardenService gardenService, ImageDownloadService imageDownloadService) {
        this.plantWikiService = plantWikiService;
        this.gardenerFormService = gardenerFormService;
        this.gardenService = gardenService;
        this.imageDownloadService = imageDownloadService;
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


    @GetMapping("/plantWiki")
    public String plantWiki(
            Model model
    ) throws IOException, URISyntaxException {
        logger.info("GET /plantWiki");
        List<WikiPlant> resultPlants = plantWikiService.getPlants("");
        model.addAttribute("resultPlants",resultPlants);

        // need to add to model so that the navbar can populate the dropdown
        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);
        List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());
        model.addAttribute("gardens", gardens);

        String imageUrl = "https://perenual.com/storage/species_image/3_abies_concolor/small/52292935430_f4f3b22614_b.jpg";

        return "plantWikiTemplate";
    }

    @PostMapping("/plantWiki")
    public String plantWikiSearch(@RequestParam("searchTerm") String searchTerm, Model model) throws IOException, URISyntaxException {

        logger.info("POST /plantWiki");
        List<WikiPlant> resultPlants = plantWikiService.getPlants(searchTerm);
        model.addAttribute("resultPlants", resultPlants);
        String errorMessage = "No plants were found";
        if(resultPlants.isEmpty()) {
            model.addAttribute("errorMessage", errorMessage);
        }
        model.addAttribute("searchTerm", searchTerm);
        return "plantWikiTemplate";
    }


    @PostMapping("/addPlant")
    public String addPlant(
            @RequestParam("gardenId") Long gardenId,
            @RequestParam("name") String name,
            @RequestParam("count") Integer count,
            @RequestParam("description") String description,
            @RequestParam("date") String date,
            @RequestParam("file") MultipartFile file,
            Model model) {

        model.addAttribute(name, "tempName");
        model.addAttribute(String.valueOf(count), 1);
        model.addAttribute(description, "tempName");
        model.addAttribute(date, "tempName");
        model.addAttribute(String.valueOf(file), "tempName");

        logger.info("POST /addPlant");

        return "plantWikiTemplate";

    }

    @PostMapping("/downloadImage")
    public String downloadImage(@RequestParam("imageUrl") String imageUrl, RedirectAttributes redirectAttributes) {
        String uniqueFilename = imageDownloadService.downloadImage(imageUrl); // Get the unique filename

        redirectAttributes.addFlashAttribute("imagePath", "/images/tempImageStorage/" + uniqueFilename);
        redirectAttributes.addFlashAttribute("showModal", true);

        return "redirect:/plantWiki";
    }



}
