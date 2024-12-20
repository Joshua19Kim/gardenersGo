package nz.ac.canterbury.seng302.gardenersgrove.controller.PlantControllers;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Long.parseLong;
@Controller
public class PlantAddFormController {
    Logger logger = LoggerFactory.getLogger(PlantAddFormController.class);

    private final PlantService plantService;
    private final GardenService gardenService;
    private final GardenerFormService gardenerFormService;
    private final ImageService imageService;
    private final RequestService requestService;
    private Gardener gardener;

    /**
     * Constructor for PlantEditFormController.
     *
     * @param plantService Service for managing plant-related operations.
     * @param gardenService Service for managing garden-related operations.
     */
    @Autowired
    public PlantAddFormController(PlantService plantService, GardenService gardenService, RequestService requestService,
                                  GardenerFormService gardenerFormService, ImageService imageService) {
        this.plantService = plantService;
        this.gardenService = gardenService;
        this.gardenerFormService = gardenerFormService;
        this.imageService = imageService;
        this.requestService = requestService;
    }

    /**
     * Displays the form for adding a new plant to a garden.
     *
     * @param gardenId The ID of the garden to which the plant is being added.
     * @param model    The model for passing data to the view.
     * @param request  The HTTP request received by the server.
     * @return The desired template based on the presence of the garden.
     */
    @GetMapping("gardens/details/plants/form")
    public String form(@RequestParam(name = "gardenId") String gardenId, Model model, HttpServletRequest request) {
        logger.info("GET /gardens/details/plants/form");

        Optional<Garden> garden = gardenService.getGarden(parseLong(gardenId));

        if (garden.isPresent()) {
            model.addAttribute("requestURI", requestService.getRequestURI(request));
            model.addAttribute("garden", garden.get());
            return "plantsFormTemplate";
        } else {
            return "redirect:/gardens";
        }
    }

    /**
     * Handles the submission of the plant form.
     *
     * @param name The name of the plant.
     * @param count The count of the plant.
     * @param description The description of the plant.
     * @param dateString The date the plant was planted.
     * @param gardenId The ID of the garden to which the plant belongs.
     * @param isDateInvalid Indication of existence of a partially inputted date e.g. "10/mm/yyyy"
     * @param model The model for passing data to the view.
     * @return The template for the plant form or redirects to the garden details page.
     */
    @PostMapping("gardens/details/plants/form")
    public String submitForm(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "count", required = false) String count,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "date", required = false) String dateString,
            @RequestParam(name = "isDateInvalid", required = false) boolean isDateInvalid,
            @RequestParam(name = "gardenId") String gardenId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request,
            Model model) {
        logger.info("/gardens/details/plants/form");

        Garden garden = gardenService.getGarden(Long.parseLong(gardenId)).get();
        String validatedPlantName = ValidityChecker.validatePlantName(name);
        String validatedPlantCount = ValidityChecker.validatePlantCount(count);
        String validatedPlantDescription = ValidityChecker.validatePlantDescription(description);

        boolean isValid = true;

        Optional<String> dateError = Optional.empty();
        if (isDateInvalid) {
            dateError = Optional.of("Date is not in valid format, DD/MM/YYYY");
            isValid = false;
        } else if (dateString != null) {
            dateError = ValidityChecker.validateDate(dateString);
            isValid = dateError.isEmpty();
        }
        model.addAttribute("dateError", dateError.orElse(""));

        if (!Objects.equals(name, validatedPlantName)) {
            model.addAttribute("nameError", validatedPlantName);
            isValid = false;
        }
        if (count != null && !Objects.equals(count.replace(",", "."), validatedPlantCount)) {
            model.addAttribute("countError", validatedPlantCount);
            isValid = false;
        }
        if (!Objects.equals(description, validatedPlantDescription)) {
            model.addAttribute("descriptionError", validatedPlantDescription);
            isValid = false;
        }
        if(!file.isEmpty()) {
            Optional<String> uploadMessage = imageService.checkValidImage(file);
            if(uploadMessage.isPresent()) {
                model.addAttribute("uploadError", uploadMessage.get());
                isValid = false;
            }
        }

        if (isValid) {
            Plant plant = new Plant(name, garden);
            boolean countPresent = count != null && !validatedPlantCount.trim().isEmpty();
            boolean descriptionPresent = description != null && !validatedPlantDescription.trim().isEmpty();

            if (countPresent) {
                plant.setCount(new BigDecimal(validatedPlantCount).stripTrailingZeros().toPlainString());
            }
            if (descriptionPresent) {
                plant.setDescription(validatedPlantDescription);
            }
            if (dateString != null) {
                LocalDate date = LocalDate.parse(dateString);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String validatedDate = date.format(formatter);

                plant.setDatePlanted(validatedDate);
            }
            plantService.addPlant(plant);
            if(file.isEmpty()) {
                plant.setImage("/images/placeholder.jpg");
                plantService.addPlant(plant);
            } else {
                imageService.savePlantImage(file, plant);
            }
            return "redirect:/gardens/details?gardenId=" + gardenId;
        } else {
            model.addAttribute("requestURI", requestService.getRequestURI(request));
            model.addAttribute("name", name);
            model.addAttribute("count", count);
            model.addAttribute("description", description);
            model.addAttribute("date", dateString);
            model.addAttribute("garden", garden);
            return "plantsFormTemplate";
        }
    }
}
