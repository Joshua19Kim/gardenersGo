package nz.ac.canterbury.seng302.gardenersgrove.controller;

import static java.lang.Long.parseLong;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.IntStream;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller class for handling HTTP requests for the Collections page
 */
@Controller
public class CollectionsController {
    private final IdentifiedPlantService identifiedPlantService;
    Logger logger = LoggerFactory.getLogger(CollectionsController.class);

    private int pageSize;

    private final ImageService imageService;

    private final GardenService gardenService;

    private final GardenerFormService gardenerFormService;

    private final RequestService requestService;
    private Gardener gardener;

    private static final String PAGINATION_MESSAGE_ATTRIBUTE = "paginationMessage";
    private static final String ERROR_OCCURRED_ATTRIBUTE = "errorOccurred";
    private static final String SHOW_MODAL_ATTRIBUTE = "showModal";
    private static final String SUCCESS_MESSAGE_ATTRIBUTE = "successMessage";
    private static final String ERROR_KEY = "error";

    private final PlantIdentificationService plantIdentificationService;

    private final Map<String, String> errorResponse;
    private final Map<String, Object> response;


    /**
     * Constructor to instantiate CollectionsController
     * @param gardenService used in conjunction with gardener form service to populate navbar
     * @param gardenerFormService used in conjunction with above to populate navbar
     */
    public CollectionsController(ImageService imageService, GardenService gardenService,
                                 GardenerFormService gardenerFormService, IdentifiedPlantService identifiedPlantService,
                                 RequestService requestService,
                                 PlantIdentificationService plantIdentificationService) {
        this.imageService = imageService;
        this.gardenService = gardenService;
        this.gardenerFormService = gardenerFormService;
        this.identifiedPlantService = identifiedPlantService;
        this.plantIdentificationService = plantIdentificationService;
        errorResponse = new HashMap<>();
        response = new HashMap<>();
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
            @RequestParam(name="savedPlant", defaultValue = "") String savedPlantId,
            Model model) {

        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        int pageNo = ValidityChecker.validatePageNumber(pageNoString);
        Page<IdentifiedPlantSpeciesImpl> speciesList = identifiedPlantService.getGardenerPlantSpeciesPaginated(pageNo, pageSize, gardener.getId());
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
            model.addAttribute(PAGINATION_MESSAGE_ATTRIBUTE, paginationMessage);
        } else {
            String paginationMessage = "Showing results 0 to 0 of 0";
            model.addAttribute(PAGINATION_MESSAGE_ATTRIBUTE, paginationMessage);
        }


        // For Autocomplete
        List<String> plantScientificNames = plantIdentificationService.getAllSpeciesScientificNames(gardener);
        List<String> plantNames = plantIdentificationService.getAllPlantNames(gardener);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String plantNamesJson = objectMapper.writeValueAsString(plantNames);
            String plantScientificNamesJson = objectMapper.writeValueAsString(plantScientificNames);
            model.addAttribute("plantNamesJson", plantNamesJson);
            model.addAttribute("plantScientificNamesJson", plantScientificNamesJson);
        } catch (JsonProcessingException e) {
            logger.error("Error converting lists to JSON", e);
        }

        if(!model.containsAttribute(ERROR_OCCURRED_ATTRIBUTE)) {
            model.addAttribute(ERROR_OCCURRED_ATTRIBUTE, false);
        }
        if (!model.containsAttribute(SHOW_MODAL_ATTRIBUTE)) {
            model.addAttribute(SHOW_MODAL_ATTRIBUTE, false);
        }

        if (!savedPlantId.isEmpty()) {
            IdentifiedPlant savedPlant = identifiedPlantService.getCollectionPlantById(Long.parseLong(savedPlantId));
            if (savedPlant != null && savedPlant.getGardener().equals(gardener)) {
                if (savedPlant.getSpeciesScientificNameWithoutAuthor().isEmpty()) {
          model.addAttribute(
              SUCCESS_MESSAGE_ATTRIBUTE,
              savedPlant.getName() + " has been added to species: No Species");
                } else {
          model.addAttribute(
              SUCCESS_MESSAGE_ATTRIBUTE,
              savedPlant.getName()
                  + " has been added to species: "
                  + savedPlant.getSpeciesScientificNameWithoutAuthor());
                }
            }
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
            model.addAttribute(PAGINATION_MESSAGE_ATTRIBUTE, paginationMessage);
        } else {
            String paginationMessage = "Showing results 0 to 0 of 0";
            model.addAttribute(PAGINATION_MESSAGE_ATTRIBUTE, paginationMessage);
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
            if (scientificName.isEmpty()) {
        redirectAttributes.addFlashAttribute(
            SUCCESS_MESSAGE_ATTRIBUTE, plantName + " has been added to species: No Species");
            } else {
        redirectAttributes.addFlashAttribute(
            SUCCESS_MESSAGE_ATTRIBUTE, plantName + " has been added to species: " + scientificName);
            }
            return "redirect:/myCollection";
        } else {
            redirectAttributes.addFlashAttribute("plantName", plantName);
            redirectAttributes.addFlashAttribute("description", description);
            redirectAttributes.addFlashAttribute("scientificName", scientificName);
            redirectAttributes.addFlashAttribute("uploadedDate", uploadedDate);
            redirectAttributes.addFlashAttribute(ERROR_OCCURRED_ATTRIBUTE, true);
            redirectAttributes.addFlashAttribute(SHOW_MODAL_ATTRIBUTE, true);

            return "redirect:/myCollection";
        }
    }

    @PostMapping("/myCollection/autoPopulate")
    @ResponseBody
    public ResponseEntity<?> autoPopulateDetails(
            @RequestBody Map<String, String> selectedInfo) {
        logger.info("POST /myCollection/autoPopulate");
        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);

        try {
            String name = selectedInfo.get("name");
            boolean isPlantName = Boolean.parseBoolean(selectedInfo.get("isPlantName"));
            boolean isSpecieScientificName = Boolean.parseBoolean(selectedInfo.get("isSpecieScientificName"));

            List<Map<String, String>> plantDetailsList;
            if (isPlantName) {
                plantDetailsList = plantIdentificationService.getPlantDetailsWithPlantNames(name);
            } else if (isSpecieScientificName) {
                plantDetailsList = plantIdentificationService.getPlantDetailsWithSpeciesScientificName(name);
            } else {
                plantDetailsList = new ArrayList<>();
            }

            if (plantDetailsList.isEmpty()) {
                errorResponse.put(ERROR_KEY, "Plant not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            ObjectMapper mapper = new ObjectMapper();
            String jsonResult = mapper.writeValueAsString(plantDetailsList);

            return ResponseEntity.ok(jsonResult);
        } catch (Exception e) {
            errorResponse.put(ERROR_KEY, "Failed to save the identified plant: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
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

        long plantId = parseLong(plantIdString, 10);
        IdentifiedPlant plant = identifiedPlantService.getCollectionPlantById(plantId);

        if (plant != null) {
            model.addAttribute("requestURI", requestService.getRequestURI(request));
            model.addAttribute("plant", plant);

            // need to add to model so that the navbar can populate the dropdown
            List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());
            model.addAttribute("gardens", gardens);

            return "editIdentifiedPlantForm";
        } else {
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

        long plantId = parseLong(plantIdString, 10);
        IdentifiedPlant plantOptional = identifiedPlantService.getCollectionPlantById(plantId);

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

        if (isValid && plantOptional != null) {
            IdentifiedPlant plant = plantOptional;

            plant.setName(validatedPlantName);
            boolean descriptionPresent = !Objects.equals(validatedPlantDescription.trim(), "");

            if (descriptionPresent) {
                plant.setDescription(validatedPlantDescription);
            } else {
                plant.setDescription(null);
            }

            identifiedPlantService.saveIdentifiedPlantDetails(plant);

            return "redirect:/collectionDetails?speciesName=" + plant.getSpeciesScientificNameWithoutAuthor();
        } else {
            Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
            gardenerOptional.ifPresent(value -> gardener = value);
            model.addAttribute("requestURI", requestService.getRequestURI(request));
            model.addAttribute("name", name);
            model.addAttribute("plant", plantOptional);
            model.addAttribute("description", description);

            return "editIdentifiedPlantForm";
        }
    }

}
