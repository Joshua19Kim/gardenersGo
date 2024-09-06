package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.InputValidationUtil;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * The controller for the plant wiki page. It handles all requests for going to the page and searching for plant information.
 */
@Controller
public class PlantWikiController {

    Logger logger = LoggerFactory.getLogger(PlantWikiController.class);

    private final PlantWikiService plantWikiService;

    private final PlantService plantService;

    private final GardenerFormService gardenerFormService;

    private final GardenService gardenService;

    private final ImageService imageService;

    private Gardener gardener;




    @Autowired
    public PlantWikiController(PlantWikiService plantWikiService, PlantService plantService, GardenerFormService gardenerFormService, GardenService gardenService, ImageService imageService) {
        this.plantWikiService = plantWikiService;
        this.plantService = plantService;
        this.gardenerFormService = gardenerFormService;
        this.gardenService = gardenService;
        this.imageService = imageService;
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
        Object result = plantWikiService.getPlants("");
        if (result instanceof List<?>) {
            model.addAttribute("resultPlants", (List<WikiPlant>) result);
        } else if (result instanceof String) {
            model.addAttribute("resultPlants", new ArrayList<>());
            model.addAttribute("errorMessage", result);
        }
        // need to add to model so that the navbar can populate the dropdown
        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);
        List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());
        model.addAttribute("gardens", gardens);

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
        String errorMessage = "No plants were found";
        logger.info("POST /plantWiki");
        Object result = plantWikiService.getPlants(searchTerm);

        if (result instanceof List<?>) {
            model.addAttribute("resultPlants", result);
            if(((List<WikiPlant>) result).isEmpty()) {
                model.addAttribute("errorMessage", errorMessage);
            }
        } else if (result instanceof String) { // If API is down
            model.addAttribute("errorMessage", result);
            model.addAttribute("resultPlants", new ArrayList<>());
        }
        model.addAttribute("searchTerm", searchTerm);

        // need to add to model so that the navbar can populate the dropdown
        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);
        List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());
        model.addAttribute("gardens", gardens);

        return "plantWikiTemplate";
    }


    /**
     * Post mapping to add a plant from the plant wiki to the garden. Repurposed plant form to create the modal in which
     * a user can add a plant straiht from the wiki. Due to it being a modal there are redirect attributes that are
     * needed in order to control the flow of this.
     *
     * @param gardenId id of the garden that you want to add the plant to
     * @param name name of the plant you are wanting to add
     * @param count quantity of plants adding to your garden
     * @param description description is autopopulated with useful information from API, can be changed at any time
     * @param date the plant is added to garden
     * @param isDateInvalid flag to check if date formatted correctly
     * @param imageUrl image url from api used for downloading once submitting
     * @param file users uploaded image for that plant
     * @param isFileUploaded flag to tell the difference between upload and image url
     * @param model the model
     * @param redirectAttributes for redirection
     * @return redirect to plantWiki page so page reloaded in correct state
     */
    @PostMapping("/addPlant")
    public String addPlant(
            @RequestParam("gardenId") Long gardenId,
            @RequestParam("name") String name,
            @RequestParam("count") String count,
            @RequestParam("description") String description,
            @RequestParam("date") String date,
            @RequestParam(value = "isDateInvalid", required = false) boolean isDateInvalid,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "isFileUploaded", required = false, defaultValue = "false") boolean isFileUploaded,
            Model model,
            RedirectAttributes redirectAttributes) {

        InputValidationUtil inputValidator = new InputValidationUtil(gardenerFormService);


        logger.info("POST /addPlant");

        // Add values to model for displaying in the form in case of errors
        model.addAttribute("name", name);
        model.addAttribute("count", count);
        model.addAttribute("description", description);
        model.addAttribute("date", date);
        model.addAttribute("imageUrl", imageUrl);

        Optional<Garden> gardenOptional = gardenService.getGarden(gardenId);
        if (gardenOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selected garden does not exist.");
            return "redirect:/plantWiki";
        }

        Garden garden = gardenOptional.get();
        boolean isValid = true;

        // Validate name, count, and description (same as before)
        String validatedPlantName = ValidityChecker.validatePlantName(name);
        String validatedPlantCount = ValidityChecker.validatePlantCount(count);
        String validatedPlantDescription = ValidityChecker.validatePlantDescription(description);

        if (!Objects.equals(name, validatedPlantName)) {
            redirectAttributes.addFlashAttribute("nameError", validatedPlantName);
            isValid = false;
        }

        if (count != null && !Objects.equals(count.replace(",", "."), validatedPlantCount)) {
            redirectAttributes.addFlashAttribute("countError", validatedPlantCount);
            isValid = false;
        }

        if (!Objects.equals(description, validatedPlantDescription)) {
            redirectAttributes.addFlashAttribute("descriptionError", validatedPlantDescription);
            isValid = false;
        }

        // Date validation similar to DoB handling in registerController
        Optional<String> dateError = Optional.empty();
        if (isDateInvalid) {
            dateError = Optional.of("Date is not in valid format, DD/MM/YYYY");
        } else {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate.parse(date, formatter); // Try to parse the date, will throw an exception if invalid
            } catch (DateTimeParseException e) {
                dateError = Optional.of("Date is not in valid format, DD/MM/YYYY");
            }
        }

        if (dateError.isPresent()) {
            redirectAttributes.addFlashAttribute("dateError", dateError.get());
            isValid = false;
        }

        // Image validation (same as before)
        if (isFileUploaded && file != null && !file.isEmpty()) {
            Optional<String> uploadMessage = imageService.checkValidImage(file);
            if (uploadMessage.isPresent()) {
                redirectAttributes.addFlashAttribute("uploadError", uploadMessage.get());
                isValid = false;
            }
        }

        if (isValid) {
            Plant plant = new Plant(name, garden);
            // Plant details setup (same as before)
            if (count != null && !count.trim().isEmpty()) {
                plant.setCount(new BigDecimal(validatedPlantCount).stripTrailingZeros().toPlainString());
            }

            if (description != null && !description.trim().isEmpty()) {
                plant.setDescription(validatedPlantDescription);
            }

            if (date != null && !date.isEmpty()) {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate parsedDate = LocalDate.parse(date, inputFormatter);
                String formattedDate = parsedDate.format(outputFormatter);
                plant.setDatePlanted(formattedDate);
            }

            plantService.addPlant(plant);

            // Image handling (same as before)
            if (isFileUploaded && file != null && !file.isEmpty()) {
                imageService.savePlantImage(file, plant);
            } else if (imageUrl != null && !imageUrl.isEmpty()) {
                if (imageUrl.equals("/images/placeholder.jpg")) {
                    plant.setImage("/images/placeholder.jpg");
                } else {
                    String filePath = imageService.downloadImage(imageUrl, plant.getId());
                    plant.setImage(filePath);
                }
            } else {
                plant.setImage("/images/placeholder.jpg");
            }

            plantService.addPlant(plant);

            redirectAttributes.addFlashAttribute("successMessage", "Plant added successfully!");
            return "redirect:/plantWiki";
        } else {
            redirectAttributes.addFlashAttribute("name", name);
            redirectAttributes.addFlashAttribute("count", count);
            redirectAttributes.addFlashAttribute("description", description);
            redirectAttributes.addFlashAttribute("date", date);
            redirectAttributes.addFlashAttribute("imageUrl", imageUrl);
            redirectAttributes.addFlashAttribute("errorOccurred", "an error has occurred");

            return "redirect:/plantWiki";
        }
    }



}



