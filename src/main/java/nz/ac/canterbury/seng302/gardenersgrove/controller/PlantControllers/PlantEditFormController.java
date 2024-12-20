package nz.ac.canterbury.seng302.gardenersgrove.controller.PlantControllers;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RequestService;
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

/**
 * This class serves as a controller for handling plant-related operations such as form submission
 * and editing.
 */
@Controller
public class PlantEditFormController {
    Logger logger = LoggerFactory.getLogger(PlantEditFormController.class);

    private final PlantService plantService;
    private final GardenService gardenService;
    private final GardenerFormService gardenerFormService;
    private final ImageService imageService;
    private final RequestService requestService;

    /**
     * Constructor for PlantEditFormController.
     *
     * @param plantService Service for managing plant-related operations.
     * @param gardenService Service for managing garden-related operations.
     */
    @Autowired
    public PlantEditFormController(PlantService plantService, GardenService gardenService, RequestService requestService,
                                   GardenerFormService gardenerFormService, ImageService imageService) {
        this.plantService = plantService;
        this.gardenService = gardenService;
        this.gardenerFormService = gardenerFormService;
        this.imageService = imageService;
        this.requestService = requestService;
    }

    /**
     * Gets the plant to edit by the id and returns the edit plant form template
     *
     * @param plantId The id of the plant to edit.
     * @param model   The model for passing data to the view.
     * @param request The HTTP request received by the server.
     * @return The edit plant form template or the gardens template if the plant does not exist.
     */
    @GetMapping("gardens/details/plants/edit")
    public String editPlant(@RequestParam(name = "plantId") String plantId, Model model, HttpServletRequest request) {
        logger.info("GET /gardens/details/plants/edit");
        Optional<Plant> plant = plantService.getPlant(parseLong(plantId));
        if (plant.isPresent()) {
            model.addAttribute("requestURI", requestService.getRequestURI(request));
            model.addAttribute("plant", plant.get());
            model.addAttribute("garden", plant.get().getGarden());

            if (plant.get().getDatePlanted() != null) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate date = LocalDate.parse(plant.get().getDatePlanted(), dateTimeFormatter);
                DateTimeFormatter htmlFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
                String formattedDate = date.format(htmlFormatter);
                model.addAttribute("formattedDate", formattedDate);
            }
            return "editPlantFormTemplate";
        } else {
            return "redirect:/gardens";
        }
    }

    /**
     * Handles the submission of the edit plant form.
     *
     * @param name The updated name of the plant.
     * @param count The updated count of the plant.
     * @param description The updated description of the plant.
     * @param dateString The updated date the plant was planted.
     * @param plantId The ID of the plant being edited.
     * @param isDateInvalid Indication of existence of a partially inputted date e.g. "10/mm/yyyy"
     * @param model The model for passing data to the view.
     * @return The template for the edit plant form or redirects to the garden details page.
     */
    @PostMapping("gardens/details/plants/edit")
    public String submitEditPlantForm(
            @RequestParam(name = "name") String name,
            @RequestParam(name = "count", required = false) String count,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "date", required = false) String dateString,
            @RequestParam(name = "isDateInvalid", required = false) boolean isDateInvalid,
            @RequestParam(name = "plantId") String plantId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request,
            Model model) {
        logger.info("POST /gardens/details/plants/edit");
        Plant plant = plantService.getPlant(parseLong(plantId)).get();
        String validatedPlantName = ValidityChecker.validatePlantName(name);
        String validatedPlantCount = ValidityChecker.validatePlantCount(count);
        String validatedPlantDescription = ValidityChecker.validatePlantDescription(description);

        boolean isValid = true;

        Optional<String> dateError = Optional.empty();
        if (isDateInvalid) {
            dateError = Optional.of("Date is not in valid format, DD/MM/YYYY");
            isValid = false;
        } else if (!dateString.isEmpty()) {
            dateError = ValidityChecker.validateDate(dateString);
            isValid = dateError.isEmpty();
            logger.info("ERRR");
            logger.info(dateError.isPresent() ? dateError.get() : "HEHEH");
        }
        model.addAttribute("dateError", dateError.orElse(""));

        if (!Objects.equals(name, validatedPlantName)) {
            model.addAttribute("nameError", validatedPlantName);
            isValid = false;
        }
        if (!Objects.equals(count.replace(",", "."), validatedPlantCount)) {
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
            plant.setName(validatedPlantName);
            boolean countPresent = !Objects.equals(validatedPlantCount.trim(), "");
            boolean descriptionPresent = !Objects.equals(validatedPlantDescription.trim(), "");

            if (countPresent) {
                plant.setCount(new BigDecimal(validatedPlantCount).stripTrailingZeros().toPlainString());
            } else {
                plant.setCount(null);
            }
            if (descriptionPresent) {
                plant.setDescription(validatedPlantDescription);
            } else {
                plant.setDescription(null);
            }
            if (!dateString.isEmpty()) {
                LocalDate date = LocalDate.parse(dateString);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String validatedDate = date.format(formatter);

                plant.setDatePlanted(validatedDate);
            } else {
                plant.setDatePlanted(null);
            }
            if(!file.isEmpty()) {
                imageService.savePlantImage(file, plant);
            } else {
                plantService.addPlant(plant);
            }
            return "redirect:/gardens/details?gardenId=" + plant.getGarden().getId();
        } else {

            model.addAttribute("requestURI", requestService.getRequestURI(request));
            model.addAttribute("name", name);
            model.addAttribute("count", count);
            model.addAttribute("description", description);
            model.addAttribute("date", dateString);
            model.addAttribute("plant", plant);
            model.addAttribute("garden", plant.getGarden());
            return "editPlantFormTemplate";
        }
    }

}
