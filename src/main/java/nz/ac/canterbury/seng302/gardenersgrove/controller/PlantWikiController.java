package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantWikiService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class PlantWikiController {

    Logger logger = LoggerFactory.getLogger(PlantWikiController.class);

    private final PlantWikiService plantWikiService;


    private String searchTerm;
    
    
    @Autowired
    public PlantWikiController(PlantWikiService plantWikiService) {
        this.plantWikiService = plantWikiService;
        this.searchTerm = "";
    }

    /**
     * Sets the search term
     * @param searchTerm stores the current search term so that it can be persistent across requests
     */
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    
    @GetMapping("/plantWiki")
    public String plantWiki(
            Model model
    ) throws IOException, URISyntaxException {
        logger.info("GET /plantWiki");
        List<WikiPlant> resultPlants = plantWikiService.getPlants("");
        model.addAttribute("resultPlants",resultPlants);
        return "plantWikiTemplate";
    }


}
