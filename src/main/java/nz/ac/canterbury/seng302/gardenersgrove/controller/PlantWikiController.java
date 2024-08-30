package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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


    @GetMapping("/plantWiki")
    public String plantWiki(
            Model model
    ) throws IOException, URISyntaxException {
        logger.info("GET /plantWiki");
        List<WikiPlant> resultPlants = plantWikiService.getPlants("");
        model.addAttribute("resultPlants",resultPlants);

        // need to add to model so that the navbar can populate the dropdown
        Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
        gardenerOptional.ifPresent(value -> gardener = value);
        List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());
        model.addAttribute("gardens", gardens);

        String imageUrl = "https://perenual.com/storage/species_image/3_abies_concolor/small/52292935430_f4f3b22614_b.jpg";

        return "plantWikiTemplate";
    }

    @PostMapping("/plantWiki")
    public String plantWikiSearch(@RequestParam("searchTerm") String searchTerm, Model model) throws IOException, URISyntaxException {

        logger.info("POST /plantWiki");
        List<WikiPlant> resultPlants = plantWikiService.getPlants(searchTerm);
        model.addAttribute("resultPlants", resultPlants);
        String errorMessage = "No plants were found";
        if(resultPlants.isEmpty()) {
            model.addAttribute("errorMessage", errorMessage);
        }
        model.addAttribute("searchTerm", searchTerm);
        return "plantWikiTemplate";
    }


    @PostMapping("/addPlant")
    public String addPlant(
            @RequestParam("gardenId") Long gardenId,
            @RequestParam("name") String name,
            @RequestParam("count") String count,
            @RequestParam("description") String description,
            @RequestParam("date") String date,
            @RequestParam(value = "isDateInvalid", required = false) boolean isDateInvalid,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "searchTerm", required = false) String searchTerm,
            @RequestParam("file") MultipartFile file,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("POST /addPlant");

        Optional<Garden> gardenOptional = gardenService.getGarden(gardenId);
        if (gardenOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Selected garden does not exist.");
            return "redirect:/plantWiki";
        }

        Garden garden = gardenOptional.get();
        String validatedPlantName = ValidityChecker.validatePlantName(name);
        String validatedPlantCount = ValidityChecker.validatePlantCount(count);
        String validatedPlantDescription = ValidityChecker.validatePlantDescription(description);

        boolean isValid = true;

        Optional<String> dateError = Optional.empty();
        if (isDateInvalid) {
            dateError = Optional.of("Date is not in valid format, DD/MM/YYYY");
            isValid = false;
        }
        model.addAttribute("DateValid", dateError.orElse(""));

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
        if (!file.isEmpty()) {
            Optional<String> uploadMessage = imageService.checkValidImage(file);
            if (uploadMessage.isPresent()) {
                model.addAttribute("uploadError", uploadMessage.get());
                isValid = false;
            }
        }

        if (isValid) {
            Plant plant = new Plant(name, garden);
            boolean countPresent = count != null && !validatedPlantCount.trim().isEmpty();
            boolean descriptionPresent = description != null && !validatedPlantDescription.trim().isEmpty();
            boolean datePresent = date != null;

            if (countPresent) {
                plant.setCount(new BigDecimal(validatedPlantCount).stripTrailingZeros().toPlainString());
            }
            if (descriptionPresent) {
                plant.setDescription(validatedPlantDescription);
            }
            if (datePresent) {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate parsedDate = LocalDate.parse(date, inputFormatter);
                String formattedDate = parsedDate.format(outputFormatter);

                plant.setDatePlanted(formattedDate);
            }

            // First need to save the plant to get the id assigned by the database
            plantService.addPlant(plant);

            if (!file.isEmpty()) {
                imageService.savePlantImage(file, plant);
            } else if (imageUrl != null && !imageUrl.isEmpty()) {
                String filePath = imageService.downloadImage(imageUrl, plant.getId());
                logger.info(filePath);
                plant.setImage(filePath);
                plantService.addPlant(plant);
            } else {
                plant.setImage("/images/placeholder.jpg");
                plantService.addPlant(plant);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Plant added successfully!");
            return "redirect:/plantWiki";
        } else {
            // Handle validation errors and re-display the form with errors
            Optional<Gardener> gardenerOptional = getGardenerFromAuthentication();
            gardenerOptional.ifPresent(value -> gardener = value);
            List<Garden> gardens = gardenService.getGardensByGardenerId(gardener.getId());
            model.addAttribute("gardens", gardens);
            model.addAttribute("name", name);
            model.addAttribute("count", count);
            model.addAttribute("description", description);
            model.addAttribute("date", date);
            model.addAttribute("garden", garden);
            return "plantWikiTemplate";
        }
    }

}



