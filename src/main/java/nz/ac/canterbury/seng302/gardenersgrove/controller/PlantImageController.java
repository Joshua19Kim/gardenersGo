package nz.ac.canterbury.seng302.gardenersgrove.controller;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static java.lang.Long.parseLong;
@Controller
public class PlantImageController {

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
    public PlantImageController(PlantService plantService, GardenService gardenService, RequestService requestService,
                                GardenerFormService gardenerFormService, ImageService imageService) {
        this.plantService = plantService;
        this.gardenService = gardenService;
        this.gardenerFormService = gardenerFormService;
        this.imageService = imageService;
        this.requestService = requestService;
    }

    /**
     * Updates the picture of the plant from the garden details page. If the picture is valid it will update the picture
     * for the plant, else it will show an error message
     * @param file the file of plant picture
     * @param model (map-like) representation of plant picture for use in thymeleaf
     * @param plantId the id of the plant to change the profile picture of
     * @return the garden details page with the updated picture or an error message
     */
    @PostMapping("gardens/details/plants/image")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam(name = "plantId") String plantId,
                                   Model model) {

        Optional<Plant> plant = plantService.getPlant(parseLong(plantId));
        if(plant.isPresent()) {
            Optional<String> uploadMessage =  imageService.savePlantImage(file, plant.get());
            if(uploadMessage.isEmpty()) {
                return "redirect:/gardens/details?gardenId=" + plant.get().getGarden().getId();
            } else {
                return "redirect:/gardens/details?uploadError=" + uploadMessage.get() + "&errorId=" + plantId +
                        "&gardenId=" + plant.get().getGarden().getId();
            }
        } else {
            return "redirect:/gardens";
        }


    }
}
