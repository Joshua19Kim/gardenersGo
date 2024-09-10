package nz.ac.canterbury.seng302.gardenersgrove.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlantSpecies;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controller class for handling HTTP requests for the Collections page
 */
@Controller
public class CollectionsController {
    private final IdentifiedPlantService identifiedPlantService;
    private final ImageService imageService;
    Logger logger = LoggerFactory.getLogger(CollectionsController.class);

    private int pageSize;

    private final GardenService gardenService;

    private final GardenerFormService gardenerFormService;

    private Gardener gardener;

    private final String paginationMessageAttribute = "paginationMessage";

    private final String errorOccurredAttribute = "errorOccurred";

    private final String showModalAttribute = "showModal";

    /**
     * Constructor to instantiate CollectionsController
     * @param gardenService used in conjunction with gardener form service to populate navbar
     * @param gardenerFormService used in conjunction with above to populate navbar
     */
    public CollectionsController(ImageService imageService, GardenService gardenService, GardenerFormService gardenerFormService, IdentifiedPlantService identifiedPlantService) {
        this.imageService = imageService;
        this.gardenService = gardenService;
        this.gardenerFormService = gardenerFormService;
        this.identifiedPlantService = identifiedPlantService;
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
        Page<IdentifiedPlantSpecies> speciesList = identifiedPlantService.getGardenerPlantSpeciesPaginated(pageNo, pageSize, gardener.getId());
        logger.info("GET /myCollection");
        model.addAttribute("speciesList", speciesList);
        int totalPages = speciesList.getTotalPages();
        if(totalPages > 0) {
            int lowerBound = Math.max(pageNo - 1, 1);
            int upperBound = Math.min(pageNo + 3, totalPages);
            List<Integer> pageNumbers = IntStream.rangeClosed(lowerBound, upperBound)
                    .boxed()
                    .toList();
            model.addAttribute("pageNumbers", pageNumbers);

            long totalItems = speciesList.getTotalElements();
            int startIndex = pageSize * pageNo + 1;
            long endIndex = Math.min((long) pageSize * (pageNo + 1), totalItems);
            String paginationMessage = "Showing results " + startIndex + " to " + endIndex + " of " + totalItems;
            model.addAttribute(paginationMessageAttribute, paginationMessage);
        } else {
            String paginationMessage = "Showing results 0 to 0 of 0";
            model.addAttribute(paginationMessageAttribute, paginationMessage);
        }


        // For Autocomplete
        List<String> plantScientificNames = plantIdentificationService.getAllSpeciesScientificNames();
        List<String> plantNames = plantIdentificationService.getAllPlantNames();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String plantNamesJson = objectMapper.writeValueAsString(plantNames);
            String plantScientificNamesJson = objectMapper.writeValueAsString(plantScientificNames);
            model.addAttribute("plantNamesJson", plantNamesJson);
            model.addAttribute("plantScientificNamesJson", plantScientificNamesJson);
        } catch (JsonProcessingException e) {
            logger.error("Error converting lists to JSON", e);
        }

        if(!model.containsAttribute(errorOccurredAttribute)) {
            model.addAttribute(errorOccurredAttribute, false);
        }
        if (!model.containsAttribute(showModalAttribute)) {
            model.addAttribute(showModalAttribute, false);
        }

        return "myCollectionTemplate";
    }


    /**
     * Displays all the plants that have been added to the collection that are a part of the specified species
     * @param speciesName the name of the species
     * @param pageNoString the page number
     * @param model the model
     * @return the collection details template
     */
    @GetMapping("/collectionDetails")
    public String getSpeciesDetails(
            @RequestParam(name = "speciesName") String speciesName,
            @RequestParam(name="pageNo", defaultValue = "0") String pageNoString,
            Model model) {

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);
        int pageNo = ValidityChecker.validatePageNumber(pageNoString);
        Page<IdentifiedPlant> collectionsList = identifiedPlantService.getGardenerPlantsBySpeciesPaginated(pageNo, pageSize, gardener.getId(), speciesName);
        model.addAttribute("collectionsList", collectionsList);
        model.addAttribute("speciesName", speciesName);

        int totalPages = collectionsList.getTotalPages();
        if(totalPages > 0) {
            int lowerBound = Math.max(pageNo - 1, 1);
            int upperBound = Math.min(pageNo + 3, totalPages);
            List<Integer> pageNumbers = IntStream.rangeClosed(lowerBound, upperBound)
                    .boxed()
                    .toList();
            model.addAttribute("pageNumbers", pageNumbers);

            long totalItems = collectionsList.getTotalElements();
            int startIndex = pageSize * pageNo + 1;
            long endIndex = Math.min((long) pageSize * (pageNo + 1), totalItems);
            String paginationMessage = "Showing results " + startIndex + " to " + endIndex + " of " + totalItems;
            model.addAttribute(paginationMessageAttribute, paginationMessage);
        } else {
            String paginationMessage = "Showing results 0 to 0 of 0";
            model.addAttribute(paginationMessageAttribute, paginationMessage);
        }

        // Add gardens to the model for the navbar
        List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());
        model.addAttribute("gardens", gardens);

        return "collectionDetailsTemplate";
    }


    /**
     * This post method is used when the user manually adds a plant to their collection. It validates all the user inputs
     * and adds error messages where appropriate.
     *
     * @param plantName the name of the plant
     * @param description the description of the plant
     * @param scientificName the scientific name of the plant
     * @param uploadedDate the date uploaded
     * @param isDateInvalid whether HTML picked up a date error or not
     * @param plantImage the image uploaded for the plant
     * @param redirectAttributes used to add flash attributes for redirection.
     * @return returns the my collection template
     */
    @PostMapping("/myCollection")
    public String addPlantToCollection(
            @RequestParam (name="plantName") String plantName,
            @RequestParam (name="description", required = false) String description,
            @RequestParam (name="scientificName", required = false) String scientificName,
            @RequestParam (name="uploadedDate", required = false) LocalDate uploadedDate,
            @RequestParam(name = "isDateInvalid", required = false) boolean isDateInvalid,
            @RequestParam ("plantImage") MultipartFile plantImage,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        logger.info("/myCollection/addNewPlantToMyCollection");
        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        String validatedPlantName = ValidityChecker.validatePlantName(plantName);
        String validatedScientificName = ValidityChecker.validateScientificPlantName(scientificName);
        String validatedPlantDescription = ValidityChecker.validatePlantDescription(description);

        boolean isValid = true;

        if (isDateInvalid) {
            String dateError = "Date is not in valid format, DD/MM/YYYY";
            redirectAttributes.addFlashAttribute("dateError", dateError);
            isValid = false;
        }

        if (!Objects.equals(plantName, validatedPlantName)) {
            redirectAttributes.addFlashAttribute("plantNameError", validatedPlantName);
            isValid = false;
        }
        if (!Objects.equals(scientificName, validatedScientificName)) {
            redirectAttributes.addFlashAttribute("scientificNameError", validatedScientificName);
            isValid = false;
        }
        if (!Objects.equals(description, validatedPlantDescription)) {
            redirectAttributes.addFlashAttribute("descriptionError", validatedPlantDescription);
            isValid = false;
        }

        if(!plantImage.isEmpty()) {
            Optional<String> uploadMessage = imageService.checkValidImage(plantImage);
            if(uploadMessage.isPresent()) {
                redirectAttributes.addFlashAttribute("uploadError", uploadMessage.get());
                isValid = false;
            }
        }

        if(isValid) {
            IdentifiedPlant identifiedPlant = new IdentifiedPlant(plantName, gardener);

            if(description != null && !description.trim().isEmpty()) {
                identifiedPlant.setDescription(description);
            }
            if(scientificName != null && !scientificName.trim().isEmpty()) {
                identifiedPlant.setSpeciesScientificNameWithoutAuthor(scientificName);
            } else {
                identifiedPlant.setSpeciesScientificNameWithoutAuthor("No Species");
            }
            if(uploadedDate != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                identifiedPlant.setDateUploaded(uploadedDate.format(formatter));
            }
            if(plantImage.isEmpty()) {
                identifiedPlant.setUploadedImage("/images/placeholder.jpg");
                identifiedPlantService.saveIdentifiedPlantDetails(identifiedPlant);
            } else {
                identifiedPlantService.saveIdentifiedPlantDetails(identifiedPlant);
                imageService.saveCollectionPlantImage(plantImage, identifiedPlant);
            }
            return "redirect:/myCollection";
        } else {
            redirectAttributes.addFlashAttribute("plantName", plantName);
            redirectAttributes.addFlashAttribute("description", description);
            redirectAttributes.addFlashAttribute("scientificName", scientificName);
            redirectAttributes.addFlashAttribute("uploadedDate", uploadedDate);
            redirectAttributes.addFlashAttribute(errorOccurredAttribute, true);
            redirectAttributes.addFlashAttribute(showModalAttribute, true);

            return "redirect:/myCollection";
        }
    }




}
