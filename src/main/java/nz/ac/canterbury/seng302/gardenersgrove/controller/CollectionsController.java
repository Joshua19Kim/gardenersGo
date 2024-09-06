package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantIdentificationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static java.lang.Long.parseLong;

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

    private final RequestService requestService;
    private Gardener gardener;

    /**
     * Constructor to instantiate CollectionsController
     * @param plantIdentificationService the service used to interact with database
     * @param gardenService used in conjunction with gardener form service to populate navbar
     * @param gardenerFormService used in conjunction with above to populate navbar
     */
    public CollectionsController(PlantIdentificationService plantIdentificationService, GardenService gardenService,
             GardenerFormService gardenerFormService, RequestService requestService) {
        this.plantIdentificationService = plantIdentificationService;
        this.gardenService = gardenService;
        this.gardenerFormService = gardenerFormService;
        this.requestService = requestService;
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
            Model model) {

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        int pageNo = ValidityChecker.validatePageNumber(pageNoString);
        Page<IdentifiedPlantSpecies> speciesList = plantIdentificationService.getGardenerPlantSpeciesPaginated(pageNo, pageSize, gardener.getId());
        logger.info("GET /myCollection");
        model.addAttribute("speciesList", speciesList);
        int totalPages = speciesList.getTotalPages();
        if(totalPages > 0) {
            int lowerBound = Math.max(pageNo - 1, 1);
            int upperBound = Math.min(pageNo + 3, totalPages);
            List<Integer> pageNumbers = IntStream.rangeClosed(lowerBound, upperBound)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);

            long totalItems = speciesList.getTotalElements();
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

    @GetMapping("/collectionDetails")
    public String getSpeciesDetails(
            @RequestParam(name = "speciesName") String speciesName,
            @RequestParam(name="pageNo", defaultValue = "0") String pageNoString,
            Model model) {

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        int pageNo = ValidityChecker.validatePageNumber(pageNoString);
        Page<IdentifiedPlant> collectionsList = plantIdentificationService.getGardenerPlantsBySpeciesPaginated(pageNo, pageSize, gardener.getId(), speciesName);
        model.addAttribute("collectionsList", collectionsList);
        model.addAttribute("speciesName", speciesName);

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

        // Add gardens to the model for the navbar
        List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());
        model.addAttribute("gardens", gardens);

        return "collectionDetailsTemplate";
    }

    /**
     *
     * @param plantIdString string version of id of plant being edited
     * @param model used for passing attributes to the view
     * @return edit form or redirect back to collection
     */
    @GetMapping("/collectionDetails/edit")
    public String editIdentifiedPlant(
            @RequestParam(name="plantId") String plantIdString,
            Model model, HttpServletRequest request) {

        logger.info("GET /collectionDetails/edit");

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        // TODO: put this in a validator
        long plantId = parseLong(plantIdString, 10);
        Optional<IdentifiedPlant> plant = plantIdentificationService.getCollectionPlantById(plantId);

        if (plant.isPresent()) {
            model.addAttribute("requestURI", requestService.getRequestURI(request));
            model.addAttribute("plant", plant.get());

            // need to add to model so that the navbar can populate the dropdown
            List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());
            model.addAttribute("gardens", gardens);

            return "editIdentifiedPlantForm";
        } else {
            // TODO: change redirect?
            return "redirect:/collectionDetails";
        }
    }

    /**
     * Handles the submission of the edit plant form.
     *
     * @param name The updated name of the plant.
     * @param description The updated description of the plant.
     * @param plantIdString The string ID of the plant being edited.
     * @param model The model for passing data to the view.
     * @return The template for the edit plant form or redirects to the garden details page.
     */
    @PostMapping("collectionDetails/edit")
    public String submitEditIdentifiedPlantForm(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "plantId") String plantIdString,
            HttpServletRequest request,
            Model model) {

        logger.info("POST /collectionDetails/edit");

        // TODO: put this in a validator
        long plantId = parseLong(plantIdString, 10);
        Optional<IdentifiedPlant> plantOptional = plantIdentificationService.getCollectionPlantById(plantId);

        String validatedPlantName = ValidityChecker.validateIdentifiedPlantName(name);
        String validatedPlantDescription = ValidityChecker.validateIdentifiedPlantDescription(description);

        boolean isValid = true;

        if (!Objects.equals(name, validatedPlantName)) {
            model.addAttribute("nameError", validatedPlantName);
            isValid = false;
        }
        if (!Objects.equals(description, validatedPlantDescription)) {
            model.addAttribute("descriptionError", validatedPlantDescription);
            isValid = false;
        }

        if (isValid && plantOptional.isPresent()) {
            IdentifiedPlant plant = plantOptional.get();

            plant.setName(validatedPlantName);
            boolean descriptionPresent = !Objects.equals(validatedPlantDescription.trim(), "");

            if (descriptionPresent) {
                plant.setDescription(validatedPlantDescription);
            } else {
                plant.setDescription(null);
            }

            plantIdentificationService.saveIdentifiedPlantDetails(plant);

            return "redirect:/collectionDetails?speciesName=" + plant.getSpeciesScientificNameWithoutAuthor();
        } else {
            Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
            gardenerOptional.ifPresent(value -> gardener = value);
            model.addAttribute("requestURI", requestService.getRequestURI(request));
            model.addAttribute("name", name);
            model.addAttribute("plant", plantOptional.get());
            model.addAttribute("description", description);
            // TODO: Fix css bug with error messages
            return "editIdentifiedPlantForm";
        }
    }
}
