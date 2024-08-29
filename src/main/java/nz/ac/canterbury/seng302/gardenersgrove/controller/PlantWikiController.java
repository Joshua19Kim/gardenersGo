package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantWikiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
/**
 * Controller class that handles all the logic for the plant wiki page
 */
@Controller
public class PlantWikiController {

    Logger logger = LoggerFactory.getLogger(PlantWikiController.class);

    private final PlantWikiService plantWikiService;


    private String searchTerm;


    /**
     * Constructs a new PlantWikiController with the service required for searching and returning plants.
     * @param plantWikiService the service for handling reqests to the Perenual plant API
     */
    @Autowired
    public PlantWikiController(PlantWikiService plantWikiService) {
        this.plantWikiService = plantWikiService;
        this.searchTerm = "";
    }

    /**
     * Sets the search term
     *
     * @param searchTerm stores the current search term so that it can be persistent across requests
     */
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    /**
     * Handles GET requests to the /plantWiki endpoint.
     * Queries the Perenual API for plant data using the PlantWikiService
     * and adds the results to the model for rendering in the plant wiki.
     *
     * @param model The Model object to pass data to the view.
     * @return The Plant wiki template to be rendered
     * @throws IOException        If there is an error during the API call.
     * @throws URISyntaxException If there is an error with the URI syntax.
     */
    @GetMapping("/plantWiki")
    public String plantWiki(
            Model model
    ) throws IOException, URISyntaxException {
        logger.info("GET /plantWiki");
        List<WikiPlant> resultPlants = plantWikiService.getPlants("");
        model.addAttribute("resultPlants", resultPlants);
        return "plantWikiTemplate";
    }


}
