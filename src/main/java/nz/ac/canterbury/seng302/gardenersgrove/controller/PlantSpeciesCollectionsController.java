package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantSpecies;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantSpeciesService;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controller class for handling HTTP requests for the Collections page
 */
@Controller
public class PlantSpeciesCollectionsController {

    private final PlantSpeciesService plantSpeciesService;

    Logger logger = LoggerFactory.getLogger(PlantSpeciesCollectionsController.class);

    private int pageSize;

    private final GardenService gardenService;

    private final GardenerFormService gardenerFormService;

    private Gardener gardener;



    /**
     * Constructor to instantiate PlantSpeciesCollectionsController
     * @param plantSpeciesService the service used to interact with database
     * @param gardenService used in conjuction with gardener form service to populate navbar
     * @param gardenerFormService used in conjuction with above to populate navbar
     */
    @Autowired
    public PlantSpeciesCollectionsController(PlantSpeciesService plantSpeciesService, GardenerFormService gardenerFormService, GardenService gardenService) {
        this.plantSpeciesService = plantSpeciesService;
        this.gardenerFormService = gardenerFormService;
        this.gardenService = gardenService;
        pageSize = 12;
    }


    /**
     * Handles GET requests for /myCollection stub and returns the template for
     * my collections page
     *
     * @param pageNoString string representation of the page number used for
     *                     pagination
     * @param model used for passing attributes to the view
     * @return myCollectionTemplate
     */
    @GetMapping("/myCollection")
    public String getMyCollection(
            @RequestParam(name="pageNo", defaultValue = "0") String pageNoString,
            Model model) {
        int pageNo = ValidityChecker.validatePageNumber(pageNoString);
        Page<PlantSpecies> plantSpeciesList = plantSpeciesService.getGardenerPlantSpeciesPaginated(pageNo, pageSize, gardener.getId());
        logger.info("GET /myCollection");
        model.addAttribute("plantSpeciesList", plantSpeciesList);
        int totalPages = plantSpeciesList.getTotalPages();
        if(totalPages > 0) {
            int lowerBound = Math.max(pageNo - 1, 1);
            int upperBound = Math.min(pageNo + 3, totalPages);
            List<Integer> pageNumbers = IntStream.rangeClosed(lowerBound, upperBound)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);

            long totalItems = plantSpeciesList.getTotalElements();
            int startIndex = pageSize * pageNo + 1;
            long endIndex = Math.min((long) pageSize * (pageNo + 1), totalItems);
            String paginationMessage = "Showing results " + startIndex + " to " + endIndex + " of " + totalItems;
            model.addAttribute("paginationMessage", paginationMessage);
        } else {
            String paginationMessage = "Showing results 0 to 0 of 0";
            model.addAttribute("paginationMessage", paginationMessage);
        }

        return "myCollectionTemplate";
    }
}
