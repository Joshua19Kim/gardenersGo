package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantIdentificationService;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controller class for handling HTTP requests for the Collections page
 */
@Controller
public class CollectionsController {
    private final PlantIdentificationService plantIdentificationService;

    Logger logger = LoggerFactory.getLogger(CollectionsController.class);

    private int pageSize;

    private final GardenService gardenService;

    private final GardenerFormService gardenerFormService;

    private Gardener gardener;

    /**
     * Constructor to instantiate CollectionsController
     * @param plantIdentificationService the service used to interact with database
     * @param gardenService used in conjunction with gardener form service to populate navbar
     * @param gardenerFormService used in conjunction with above to populate navbar
     */
    public CollectionsController(PlantIdentificationService plantIdentificationService, GardenService gardenService, GardenerFormService gardenerFormService) {
        this.plantIdentificationService = plantIdentificationService;
        this.gardenService = gardenService;
        this.gardenerFormService = gardenerFormService;
        pageSize = 12;
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
            Model model
    ) {

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);


        int pageNo = ValidityChecker.validatePageNumber(pageNoString);
        Page<IdentifiedPlant> collectionsList = plantIdentificationService.getGardenerIdentifiedPlantsPaginated(pageNo, pageSize, gardener.getId());
        logger.info("GET /myCollection");
        model.addAttribute("collectionsList", collectionsList);
        int totalPages = collectionsList.getTotalPages();
        if(totalPages > 0) {
            int lowerBound = Math.max(pageNo - 1, 1);
            int upperBound = Math.min(pageNo + 3, totalPages);
            List<Integer> pageNumbers = IntStream.rangeClosed(lowerBound, upperBound)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);

            long totalItems = collectionsList.getTotalElements();
            int startIndex = pageSize * pageNo + 1;
            long endIndex = Math.min((long) pageSize * (pageNo + 1), totalItems);
            String paginationMessage = "Showing results " + startIndex + " to " + endIndex + " of " + totalItems;
            model.addAttribute("paginationMessage", paginationMessage);
        } else {
            String paginationMessage = "Showing results 0 to 0 of 0";
            model.addAttribute("paginationMessage", paginationMessage);
        }

        // need to add to model so that the navbar can populate the dropdown
        List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());
        model.addAttribute("gardens", gardens);

        return "myCollectionTemplate";
    }

}
