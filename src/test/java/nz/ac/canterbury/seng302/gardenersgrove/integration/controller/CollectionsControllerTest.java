package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;


import nz.ac.canterbury.seng302.gardenersgrove.controller.CollectionsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Badge;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.IdentifiedPlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantIdentificationService;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CollectionsController.class)
class CollectionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlantIdentificationService plantIdentificationService;

    @MockBean
    private BadgeService badgeService;

    @MockBean
    private IdentifiedPlantService identifiedPlantService;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private RequestService requestService;

    @MockBean
    private GardenerFormService gardenerFormService;

    @MockBean
    private ImageService imageService;

    private IdentifiedPlantRepository identifiedPlantRepository;

    private Gardener gardener;

    @BeforeEach
    public void setUp() {
        gardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        gardener.setId(1L);
        when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gardener));
        when(gardenService.getGardensByGardenerId(gardener.getId())).thenReturn(new ArrayList<>());

    }

    @Test
    @WithMockUser
    void AddPlantToCollection_ValidValues_PlantAdded() throws Exception {
        String name = "My Plant";
        String species = "Plant Species";
        LocalDate date = LocalDate.of(2004, 5, 20);
        String description = "Cool plant";
        boolean isDateInvalid = false;
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "plantImage",
                "image.jpg",
                "image/jpeg",
                "image content".getBytes()
        );
        IdentifiedPlant identifiedPlant = new IdentifiedPlant(name, description, species, date, gardener);

        when(identifiedPlantService.saveIdentifiedPlantDetails(any(IdentifiedPlant.class))).thenReturn(identifiedPlant);
        doNothing().when(imageService).saveCollectionPlantImage(eq(mockMultipartFile), any(IdentifiedPlant.class));
        when(imageService.checkValidImage(mockMultipartFile)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/myCollection")
                .file(mockMultipartFile)
                .param("plantName", name)
                .param("description", description)
                .param("scientificName", species)
                .param("uploadedDate", String.valueOf(date))
                .param("isDateInvalid", String.valueOf(isDateInvalid))
                .with(csrf()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/myCollection"));
    }

    @Test
    @WithMockUser
    void AddPlantToCollection_InvalidValues_PlantNotAddedAndErrorMessagesShown() throws Exception {
        String name = "";
        String species = "Pl@nt Species";
        LocalDate date = LocalDate.of(2004, 3, 31);
        String description =  "The majestic Oak tree, known scientifically as Quercus, is a symbol of fucken strength, endurance, " +
                "and wisdom. This towering deciduous tree is a hallmark of many landscapes, with its spreading branches " +
                "and dense foliage providing shade and shelter to countless creatures. With a lifespan spanning centuries, " +
                "the Oak tree has witnessed the ebb and flow of history, its sturdy trunk bearing the scars of time. " +
                "From ancient mythologies to modern literature, the Oak tree has captivated the human imagination, " +
                "inspiring awe and reverence. Its acorns, a source of sustenance for wildlife, are also a symbol of " +
                "potential and renewal. In folklore and legend, the Oak tree is often associated with gods and spirits," +
                " embodying resilience and resilience. From the whispering leaves to the gnarled bark," +
                " every aspect of the Oak tree tells a story of resilience, adaptability, and the enduring power of nature." +
                ": Garden description must be less than 512 characters <br/>The description does not match the language standards of the app.";
        boolean isDateInvalid = true;
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "plantImage",
                "file.txt",
                "plain/text",
                "Hello World!".getBytes()
        );
        IdentifiedPlant identifiedPlant = new IdentifiedPlant(name, description, species, date, gardener);
        String uploadMessage = "Image must be of type png, jpg or svg";

        when(identifiedPlantService.saveIdentifiedPlantDetails(any(IdentifiedPlant.class))).thenReturn(identifiedPlant);
        doNothing().when(imageService).saveCollectionPlantImage(eq(mockMultipartFile), any(IdentifiedPlant.class));
        when(imageService.checkValidImage(mockMultipartFile)).thenReturn(Optional.of(uploadMessage));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/myCollection")
                        .file(mockMultipartFile)
                        .param("plantName", name)
                        .param("description", description)
                        .param("scientificName", species)
                        .param("uploadedDate", String.valueOf(date))
                        .param("isDateInvalid", String.valueOf(isDateInvalid))
                        .with(csrf()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/myCollection"))
                .andExpect(flash().attribute("dateError", "Date is not in valid format, DD/MM/YYYY"))
                .andExpect(flash().attribute("plantNameError", "Plant name cannot be empty and must only " +
                        "include letters, numbers, spaces, dots, hyphens or apostrophes <br/>"))
                .andExpect(flash().attribute("scientificNameError", "Scientific name must only include " +
                        "letters, numbers, spaces, dots, hyphens or apostrophes <br/>"))
                .andExpect(flash().attribute("descriptionError", "Plant description must be less than 512 characters"))
                .andExpect(flash().attribute("uploadError", uploadMessage))
                .andExpect(flash().attribute("plantName", name))
                .andExpect(flash().attribute("description", description))
                .andExpect(flash().attribute("scientificName", species))
                .andExpect(flash().attribute("uploadedDate", date))
                .andExpect(flash().attribute("errorOccurred", true));
    }

}
