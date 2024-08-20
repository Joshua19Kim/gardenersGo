package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class PlantWikiController {

    Logger logger = LoggerFactory.getLogger(PlantWikiController.class);

    private final PlantWikiService plantWikiService;

    private final int pageSize;


    private String searchTerm;
    
    
    @Autowired
    public PlantWikiController(PlantWikiService plantWikiService) {
        this.plantWikiService = plantWikiService;
        this.pageSize = 10;
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
    ) {



        return "plantWikiTemplate";
    }

    @PostMapping("/browseGardens")
    public String sendSearchTerm(
            @RequestParam(name="pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name="pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name="searchTerm") String searchTerm,
            Model model) {
        logger.info("POST /plantWiki");
        setSearchTerm(searchTerm);

        Page<Garden> plantsPage = plantWikiService.getSearchResultsPaginated(pageNo, pageSize, searchTerm);
        if (plantsPage.getContent().isEmpty()) {
            model.addAttribute("noSearchResults", "No plants match your search.");
        }
        model.addAttribute("plantsPage", plantsPage);
        int totalPages = plantsPage.getTotalPages();
        if(totalPages > 0) {
            int lowerBound = Math.max(pageNo - 1, 1);
            int upperBound = Math.min(pageNo + 3, totalPages);
            List<Integer> pageNumbers = IntStream.rangeClosed(lowerBound, upperBound)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);

            long totalItems = plantsPage.getTotalElements();
            int startIndex = pageSize * pageNo + 1;
            long endIndex = Math.min((long) pageSize * (pageNo + 1), totalItems);
            String paginationMessage = "Showing results " + startIndex + " to " + endIndex + " of " + totalItems;
            model.addAttribute("paginationMessage", paginationMessage);
        } else {
            String paginationMessage = "Showing results 0 to 0 of 0";
            model.addAttribute("paginationMessage", paginationMessage);
        }


        return "plantWikiTemplate";
    }

}
