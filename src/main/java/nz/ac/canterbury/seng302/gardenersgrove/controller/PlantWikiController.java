package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantWikiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * The controller for the plant wiki page. It handles all requests for going to the page and searching for plant information.
 */
@Controller
public class PlantWikiController {

    Logger logger = LoggerFactory.getLogger(PlantWikiController.class);

    private final PlantWikiService plantWikiService;
    
    @Autowired
    public PlantWikiController(PlantWikiService plantWikiService) {
        this.plantWikiService = plantWikiService;
    }


    /**
     * The main method to get to the plant wiki page
     * @param model the model which has all the necessary attributes
     * @return the html template that displays all the plant information
     * @throws IOException
     * @throws URISyntaxException
     */
    @GetMapping("/plantWiki")
    public String plantWiki(
            Model model
    ) throws IOException, URISyntaxException {
        logger.info("GET /plantWiki");
        List<WikiPlant> resultPlants = plantWikiService.getPlants("");
        model.addAttribute("resultPlants",resultPlants);
        return "plantWikiTemplate";
    }

    /**
     * The post request when the user searches for the plant information. It queries the API and returns matching plants.
     * If no plants are found it will display an error message
     * @param searchTerm the term that the user entered in the search bar
     * @param model the model which has all the necessary attributes
     * @return the html template that displays all the plant information
     * @throws IOException
     * @throws URISyntaxException
     */
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


}
