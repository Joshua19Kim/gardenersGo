package nz.ac.canterbury.seng302.gardenersgrove.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.lang.Long.parseLong;

/**
 * This class serves as a controller for handling plant-related operations such as form submission
 * and editing.
 */
@Controller
public class PlantFormController {
  Logger logger = LoggerFactory.getLogger(PlantFormController.class);

  private final PlantService plantService;
  private final GardenService gardenService;
  private final ImageService imageService;

  /**
   * Constructor for PlantFormController.
   *
   * @param plantService Service for managing plant-related operations.
   * @param gardenService Service for managing garden-related operations.
   */
  @Autowired
  public PlantFormController(PlantService plantService, GardenService gardenService, ImageService imageService) {
    this.plantService = plantService;
    this.gardenService = gardenService;
    this.imageService = imageService;
  }

  /**
   * Displays the form for adding a new plant to a garden.
   *
   * @param gardenId The ID of the garden to which the plant is being added.
   * @param model The model for passing data to the view.
   * @param request The HTTP request received by the server.
   * @return The desired template based on the presence of the garden.
   */
  @GetMapping("gardens/details/plants/form")
  public String form(@RequestParam(name = "gardenId") String gardenId, Model model, HttpServletRequest request) {
    logger.info("GET /gardens/details/plants/form");
    List<Garden> gardens = gardenService.getGardenResults();
    model.addAttribute("gardens", gardens);
    Optional<Garden> garden = gardenService.getGarden(parseLong(gardenId));
    if (garden.isPresent()) {
      String requestUri = request.getRequestURI();
      String queryString = request.getQueryString();
      if (queryString != null) {
        requestUri = requestUri + "?" + queryString;
      }
      model.addAttribute("requestURI", requestUri);
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
   * @param date The date the plant was planted.
   * @param gardenId The ID of the garden to which the plant belongs.
   * @param model The model for passing data to the view.
   * @return The template for the plant form or redirects to the garden details page.
   */
  @PostMapping("gardens/details/plants/form")
  public String submitForm(
      @RequestParam(name = "name") String name,
      @RequestParam(name = "count", required = false) String count,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "date", required = false) String date,
      @RequestParam(name = "gardenId") String gardenId,
      @RequestParam("file") MultipartFile file,
      Model model) {
    logger.info("/gardens/details/plants/form");
    String validatedDate = "";
    if (!date.trim().isEmpty()) {
      LocalDate localDate = LocalDate.parse(date);
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
      validatedDate = localDate.format(formatter);
    }

    Garden garden = gardenService.getGarden(Long.parseLong(gardenId)).get();
    String validatedPlantName = ValidityChecker.validatePlantName(name);
    String validatedPlantCount = ValidityChecker.validatePlantCount(count);
    String validatedPlantDescription = ValidityChecker.validatePlantDescription(description);

    boolean isValid = true;

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
      Plant plant;
      boolean countPresent = !Objects.equals(validatedPlantCount.trim(), "");
      boolean descriptionPresent = !Objects.equals(validatedPlantDescription.trim(), "");
      boolean datePresent = !Objects.equals(date.trim(), "");

      if (countPresent && descriptionPresent && datePresent) {
        // All optional fields are present
        plant =
            new Plant(
                name,
                Float.parseFloat(validatedPlantCount),
                validatedPlantDescription,
                validatedDate,
                garden);
      } else if (countPresent && descriptionPresent) {
        // Count and Description are present
        plant =
            new Plant(
                name, Float.parseFloat(validatedPlantCount), validatedPlantDescription, garden);
      } else if (countPresent && datePresent) {
        // Count and Date are present
        plant = new Plant(name, validatedDate, Float.parseFloat(validatedPlantCount), garden);
      } else if (descriptionPresent && datePresent) {
        // Description and Date are present
        plant = new Plant(name, validatedPlantDescription, validatedDate, garden);
      } else if (countPresent) {
        // Only Count is present
        plant = new Plant(name, Float.parseFloat(validatedPlantCount), garden);
      } else if (descriptionPresent) {
        // Only Description is present
        plant = new Plant(name, validatedPlantDescription, garden);
      } else if (datePresent) {
        // Only Date is present
        plant = new Plant(name, garden, validatedDate);
      } else {
        // Only name is present
        plant = new Plant(name, garden);
      }
      plantService.addPlant(plant);
      if(file.isEmpty()) {
        plant.setImage("placeholder.jpg");
        plantService.addPlant(plant);
      } else {
        imageService.savePlantImage(file, plant);
      }
      return "redirect:/gardens/details?gardenId=" + gardenId;
    } else {
      List<Garden> gardens = gardenService.getGardenResults();
      model.addAttribute("gardens", gardens);
      model.addAttribute("name", name);
      model.addAttribute("count", count);
      model.addAttribute("description", description);
      model.addAttribute("date", date);
      model.addAttribute("garden", garden);
      return "plantsFormTemplate";
    }
  }

  /**
   * Gets the plant to edit by the id and returns the edit plant form template
   *
   * @param plantId The id of the plant to edit.
   * @param model The model for passing data to the view.
   * @param request The HTTP request received by the server.
   * @return The edit plant form template or the gardens template if the plant does not exist.
   */
  @GetMapping("gardens/details/plants/edit")
  public String editPlant(@RequestParam(name = "plantId") String plantId, Model model, HttpServletRequest request) {
    logger.info("GET /gardens/details/plants/edit");
    List<Garden> gardens = gardenService.getGardenResults();
    model.addAttribute("gardens", gardens);
    Optional<Plant> plant = plantService.getPlant(parseLong(plantId));
    if (plant.isPresent()) {
      String requestUri = request.getRequestURI();
      String queryString = request.getQueryString();
      if (queryString != null) {
        requestUri = requestUri + "?" + queryString;
      }
      model.addAttribute("requestURI", requestUri);
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
   * @param date The updated date the plant was planted.
   * @param plantId The ID of the plant being edited.
   * @param model The model for passing data to the view.
   * @return The template for the edit plant form or redirects to the garden details page.
   */
  @PostMapping("gardens/details/plants/edit")
  public String submitEditPlantForm(
      @RequestParam(name = "name") String name,
      @RequestParam(name = "count", required = false) String count,
      @RequestParam(name = "description", required = false) String description,
      @RequestParam(name = "date", required = false) String date,
      @RequestParam(name = "plantId") String plantId,
      Model model) {
    logger.info("POST /gardens/details/plants/edit");
    String formattedDate = "";
    if (!date.trim().isEmpty()) {
      LocalDate localDate = LocalDate.parse(date);
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
      formattedDate = localDate.format(formatter);
    }

    Plant plant = plantService.getPlant(parseLong(plantId)).get();
    String validatedPlantName = ValidityChecker.validatePlantName(name);
    String validatedPlantCount = ValidityChecker.validatePlantCount(count);
    String validatedPlantDescription = ValidityChecker.validatePlantDescription(description);

    boolean isValid = true;

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

    if (isValid) {
      plant.setName(validatedPlantName);
      boolean countPresent = !Objects.equals(validatedPlantCount.trim(), "");
      boolean descriptionPresent = !Objects.equals(validatedPlantDescription.trim(), "");
      boolean datePresent = !Objects.equals(date.trim(), "");
      if (countPresent) {
        plant.setCount(Float.parseFloat(validatedPlantCount));
      } else {
        plant.setCount(0);
      }
      if (descriptionPresent) {
        plant.setDescription(validatedPlantDescription);
      } else {
        plant.setDescription(null);
      }
      if (datePresent) {
        plant.setDatePlanted(formattedDate);
      } else {
        plant.setDatePlanted(null);
      }
      plantService.addPlant(plant);
      return "redirect:/gardens/details?gardenId=" + plant.getGarden().getId();
    } else {
      List<Garden> gardens = gardenService.getGardenResults();
      model.addAttribute("gardens", gardens);
      model.addAttribute("name", name);
      model.addAttribute("count", count);
      model.addAttribute("description", description);
      model.addAttribute("date", date);
      model.addAttribute("plant", plant);
      model.addAttribute("garden", plant.getGarden());
      return "editPlantFormTemplate";
    }
  }

  /**
   * Check whether there is the authentication of current user to change the profile photo.
   * If yes,read the uploaded file from user.html and Save the file.
   * If the file is empty, redirect user to 'user' page with existing image(or default photo).
   * If there is an image file, go back to 'user' page with new image
   * @param file the file of profile picture
   * @param model (map-like) representation of profile picture for use in thymeleaf
   * @return thymeleaf 'user' page after updating successfully to reload user's details, otherwise thymeleaf login page
   */
  @PostMapping("gardens/details/plants/image")
  public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                 @RequestParam(name = "plantId") String plantId,
                                 Model model) {

    Optional<Plant> plant = plantService.getPlant(parseLong(plantId));
    logger.info(plantId);
    if(plant.isPresent()) {
      Optional<String> uploadMessage =  imageService.savePlantImage(file, plant.get());
      if(uploadMessage.isEmpty()) {
        return "redirect:/gardens/details?gardenId=" + plant.get().getGarden().getId();
      } else {
          model.addAttribute("uploadError", uploadMessage.get());
          return "gardenDetailsTemplate";
      }
    } else {
      return "redirect:/gardens";
    }


  }
}
