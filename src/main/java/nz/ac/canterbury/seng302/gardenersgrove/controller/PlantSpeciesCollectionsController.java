package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantSpecies;
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

@Controller
public class PlantSpeciesCollectionsController {

    private final PlantSpeciesService plantSpeciesService;

    Logger logger = LoggerFactory.getLogger(PlantSpeciesCollectionsController.class);

    private int pageSize;


    @Autowired
    public PlantSpeciesCollectionsController(PlantSpeciesService plantSpeciesService) {
        this.plantSpeciesService = plantSpeciesService;
        pageSize = 12;
    }

    @GetMapping("/myCollection")
    public String getMyCollection(
            @RequestParam(name="pageNo", defaultValue = "0") String pageNoString,
            Model model
    ) {
        int pageNo = ValidityChecker.validatePageNumber(pageNoString);
        Page<PlantSpecies> plantSpeciesList = plantSpeciesService.getAllPlantSpeciesPaginated(pageNo, pageSize);
        logger.info(plantSpeciesList.toString());
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
