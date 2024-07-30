package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.PlantControllers.PlantImageController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {GardenFormController.class, PlantImageController.class})
public class PlantImageControllerTest {
    Gardener testGardener = new Gardener("Test", "Gardener",
            LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
            "Password1!");

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GardenService gardenService;

    @MockBean
    // This is not explicitly used but is necessary for adding gardeners to the repository for testing
    private GardenerFormService gardenerFormService;

    @MockBean
    private PlantService plantService;

    @MockBean
    private ImageService imageService;
    @MockBean
    private RequestService requestService;
    @MockBean
    private RelationshipService relationshipService;

    @MockBean
    private TagService tagService;

    @MockBean
    private WeatherService weatherService;

    @Test
    @WithMockUser
    public void ImageUploaded_ValidImage_PlantImageUpdated() throws Exception {
        Garden garden = new Garden("Test garden", "Ilam", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        String plantId = "1";
        Plant plant = new Plant("My Plant", "2", "Rose", "10/10/2023", garden);
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "image content".getBytes()
        );
        when(plantService.getPlant(Long.parseLong(plantId))).thenReturn(Optional.of(plant));
        when(imageService.savePlantImage(mockMultipartFile, plant)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/image")
                        .file(mockMultipartFile)
                        .param("plantId", plantId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=" + garden.getId()));

    }

    @Test
    @WithMockUser
    public void ImageUploaded_InvalidImage_ErrorMessageShown() throws Exception {
        Garden garden = new Garden("Test garden", "Ilam", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        String plantId = "1";
        Plant plant = new Plant("My Plant", "2", "Rose", "10/10/2023", garden);
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "file.txt",
                "plain/text",
                "Hello World!".getBytes()
        );
        String uploadMessage = "Image must be of type png, jpg or svg";
        when(plantService.getPlant(Long.parseLong(plantId))).thenReturn(Optional.of(plant));
        when(imageService.savePlantImage(mockMultipartFile, plant)).thenReturn(Optional.of(uploadMessage));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/image")
                        .file(mockMultipartFile)
                        .param("plantId", plantId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?uploadError=" + uploadMessage + "&errorId=" + plantId +
                        "&gardenId=" + garden.getId()));

    }
}
