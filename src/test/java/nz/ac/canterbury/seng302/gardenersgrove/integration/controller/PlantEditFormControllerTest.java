package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.PlantEditFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {GardenFormController.class, PlantEditFormController.class})
public class PlantEditFormControllerTest {
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
    public void EditPlantFormRequested_NonExistentPlantId_GoBackToMyGardens() throws Exception {
        String plantId = "1";
        when(gardenService.getGardenResults()).thenReturn(new ArrayList<>());
        when(plantService.getPlant(Long.parseLong(plantId))).thenReturn(Optional.empty());

        mockMvc.perform((MockMvcRequestBuilders.get("/gardens/details/plants/edit")
                        .param("plantId", plantId)
                        .with(csrf())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens"));

    }

    @Test
    @WithMockUser
    public void EditPlantFormRequested_ExistentPlantId_GoToEditPlantForm() throws Exception {
        Garden garden = new Garden("Test garden", "Ilam", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        Plant plant = new Plant("My Plant", "2", "Rose", "12/06/2004", garden);
        String plantId = "2";
        List<Garden> gardens = new ArrayList<>();
        when(gardenService.getGardenResults()).thenReturn(gardens);
        when(plantService.getPlant(Long.parseLong(plantId))).thenReturn(Optional.of(plant));
        when(requestService.getRequestURI(any(HttpServletRequest.class))).thenReturn("/gardens/details/plants/edit");

        mockMvc.perform(MockMvcRequestBuilders.get("/gardens/details/plants/edit")
                        .param("plantId", plantId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("editPlantFormTemplate"))
                .andExpect(model().attributeExists("plant", "garden", "gardens", "requestURI"))
                .andExpect(model().attribute("plant", plant))
                .andExpect(model().attribute("garden", garden))
                .andExpect(model().attribute("gardens", gardens))
                .andExpect(model().attribute("requestURI", "/gardens/details/plants/edit"));
    }

    @Test
    @WithMockUser
    public void EditPlantFormSubmitted_AllInvalid_AllErrorMessagesAdded() throws Exception {
        Garden garden = new Garden("Test garden", "Ilam", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        String name = "My Pl@nt";
        String plantId = "1";
        String count = "two";
        String description = "On the other hand, we denounce with righteous indignation and dislike men who are so " +
                "beguiled and demoralized by the charms of pleasure of the moment, so blinded by desire, that they " +
                "cannot foresee the pain and trouble that are bound to ensue; and equal blame belongs to those who " +
                "fail in their duty through weakness of will, which is the same as saying through shrinking from " +
                "toil and pain. These cases are perfectly simple and easy to distinguish. In a free hour, when our " +
                "power of choice is untrammelled and when nothing prevents our being able to do what we like best, " +
                "every pleasure is to be welcomed and every pain avoided. But in certain circumstances and owing to " +
                "the claims of duty or the obligations of business it will frequently occur that pleasures have to " +
                "be repudiated and annoyances accepted. The wise man therefore always holds in these matters to this " +
                "principle of selection: he rejects pleasures to secure other greater pleasures, or else he endures " +
                "pains to avoid worse pains.";
        String date = "10/10/2023";
        Plant plant = new Plant("My Plant", "2", "Rose", date, garden);
        when(plantService.getPlant(Long.parseLong(plantId))).thenReturn(Optional.of(plant));

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "file.txt",
                "plain/text",
                "Hello World!".getBytes()
        );
        String uploadMessage = "Image must be of type png, jpg or svg";
        when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(testGardener));
        when(imageService.checkValidImage(mockMultipartFile)).thenReturn(Optional.of(uploadMessage));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/edit")
                        .file(mockMultipartFile)
                        .param("name", name)
                        .param("count", count)
                        .param("description", description)
                        .param("date", "2023-10-10")
                        .param("plantId", plantId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("editPlantFormTemplate"))
                .andExpect(model().attributeExists("nameError", "countError", "descriptionError", "uploadError", "name", "count", "description", "date", "plant", "garden"))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("count", count))
                .andExpect(model().attribute("description", description))
                .andExpect(model().attribute("date", "2023-10-10"))
                .andExpect(model().attribute("nameError", "Plant name cannot by empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes"))
                .andExpect(model().attribute("countError", "Plant count must be a positive number"))
                .andExpect(model().attribute("descriptionError", "Plant description must be less than 512 characters"))
                .andExpect(model().attribute("plant", plant))
                .andExpect(model().attribute("uploadError", uploadMessage))
                .andExpect(model().attribute("garden", garden));

        verify(plantService, never()).addPlant(plant);
        verify(imageService, never()).savePlantImage(mockMultipartFile, plant);

    }

    @Test
    @WithMockUser
    public void EditPlantFormSubmitted_AllValidChanges_PlantUpdatedAndBackToGardenDetails() throws Exception {
        Garden garden = new Garden("Test garden", "Ilam", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        String name = "My Plant 2";
        String plantId = "1";
        String count = "3";
        String description = "Daisy";
        String date = "10/03/2024";
        Plant plant = new Plant("My Plant", "2", "Rose", "10/10/2023", garden);
        when(plantService.getPlant(Long.parseLong(plantId))).thenReturn(Optional.of(plant));
        when(plantService.addPlant(plant)).thenReturn(plant);

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "image content".getBytes()
        );
        when(imageService.savePlantImage(mockMultipartFile, plant)).thenReturn(Optional.empty());
        when(imageService.checkValidImage(mockMultipartFile)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/edit")
                        .file(mockMultipartFile)
                        .param("name", name)
                        .param("count", count)
                        .param("description", description)
                        .param("date", "2024-03-10")
                        .param("plantId", plantId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=" + garden.getId()));
        verify(imageService, times(1)).savePlantImage(mockMultipartFile, plant);
        Assertions.assertEquals(name, plant.getName());
        Assertions.assertEquals(count, plant.getCount());
        Assertions.assertEquals(description, plant.getDescription());
        Assertions.assertEquals(date, plant.getDatePlanted());
    }


}
