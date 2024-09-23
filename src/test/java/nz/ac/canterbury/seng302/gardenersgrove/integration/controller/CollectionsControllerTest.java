package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;


import nz.ac.canterbury.seng302.gardenersgrove.controller.CollectionsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
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
        when(identifiedPlantService.getCollectionPlantCount(gardener.getId())).thenReturn(1);
        when(identifiedPlantService.getSpeciesCount(gardener.getId())).thenReturn(0);

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


        verify(badgeService, times(1)).checkPlantBadgeToBeAdded(gardener, 1);
        verify(identifiedPlantService, times(1)).getCollectionPlantCount(gardener.getId());
        verify(badgeService, times(0)).checkSpeciesBadgeToBeAdded(eq(gardener), anyInt());
        verify(identifiedPlantService, times(2)).getSpeciesCount(gardener.getId());
    }

    @Test
    @WithMockUser
    void AddPlantToCollection_ValidValues_PlantAddedAndSuccessMessageShown() throws Exception {
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
                .andExpect(redirectedUrl("/myCollection"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage", "My Plant has been added to species: Plant Species"));
    }

    @Test
    @WithMockUser
    void GetCollection_SavedPlantInURL_SuccessMessageShown() throws Exception {
        String name = "My Plant";
        String species = "Plant Species";
        LocalDate date = LocalDate.of(2004, 5, 20);
        String description = "Cool plant";
        IdentifiedPlant identifiedPlant = new IdentifiedPlant(name, description, species, date, gardener);
        identifiedPlant.setId(1L);

        IdentifiedPlantSpeciesImpl plantSpecies = mock(IdentifiedPlantSpeciesImpl.class);
        Page<IdentifiedPlantSpeciesImpl> speciesList = new PageImpl<>(List.of(plantSpecies), PageRequest.of(0, 12), 1);
        when(identifiedPlantService.getGardenerPlantSpeciesPaginated(eq(0), eq(12), any(Long.class))).thenReturn(speciesList);
        when(identifiedPlantService.getCollectionPlantById(anyLong())).thenReturn(identifiedPlant);


        mockMvc.perform(MockMvcRequestBuilders.get("/myCollection")
                .param("savedPlant", identifiedPlant.getId().toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attributeExists("successMessage"))
                .andExpect(model().attribute("successMessage", "My Plant has been added to species: Plant Species"));
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

        verify(badgeService, never()).checkPlantBadgeToBeAdded(eq(gardener), anyInt());
        verify(identifiedPlantService, never()).getCollectionPlantCount(gardener.getId());
        verify(badgeService, never()).checkSpeciesBadgeToBeAdded(eq(gardener), anyInt());
        verify(identifiedPlantService, never()).getSpeciesCount(gardener.getId());
    }


    @Test
    @WithMockUser
    public void GetMyCollection_PlantAndBadgeSpecified_MyCollectionShown() throws Exception {
        Page<IdentifiedPlantSpeciesImpl> page = new PageImpl<>(List.of());
        when(identifiedPlantService.getGardenerPlantSpeciesPaginated(0, 12, gardener.getId())).thenReturn(page);



        Badge plantBadge = new Badge("plant badge", LocalDate.of(2003, 12, 30),
                BadgeType.PLANTS, gardener, "images/placeholder.jpg");
        Badge speciesBadge = new Badge("species badge", LocalDate.of(2003, 12, 30),
                BadgeType.SPECIES, gardener, "images/placeholder.jpg");

        when(badgeService.getMyBadgeById(1L, gardener.getId())).thenReturn(Optional.of(plantBadge));
        when(badgeService.getMyBadgeById(2L, gardener.getId())).thenReturn(Optional.of(speciesBadge));

        mockMvc.perform(MockMvcRequestBuilders.get("/myCollection")
                .param("plantBadgeId", "1")
                .param("speciesBadgeId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("myCollectionTemplate"))
                .andExpect(model().attribute("plantBadge", plantBadge))
                .andExpect(model().attribute("badgeCount", 2))
                .andExpect(model().attribute("speciesBadge", speciesBadge));
    }

}
