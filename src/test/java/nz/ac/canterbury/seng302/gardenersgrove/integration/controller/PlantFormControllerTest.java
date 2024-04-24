package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.PlantFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import static org.hamcrest.Matchers.empty;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {GardenFormController.class, PlantFormController.class})
public class PlantFormControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private PlantService plantService;

    @MockBean
    private ImageService imageService;

    @Test
    @WithMockUser
    public void GardenDetailsRequested_ExistentIdGiven_PlantDetailsProvided() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        Plant plant = new Plant("My Plant", garden);
        garden.getPlants().add(plant);
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        mockMvc
                .perform(MockMvcRequestBuilders.get("/gardens/details").param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("My Plant")));
    }

    @Test
    @WithMockUser
    public void PlantFormDisplayed_DefaultValues_ModelAttributesPresent() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        mockMvc
                .perform(MockMvcRequestBuilders.get("/gardens/details/plants/form").param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("plantsFormTemplate"))
                .andExpect(content().string(containsString("Name:")))
                .andExpect(content().string(containsString("Plant Count:")))
                .andExpect(content().string(containsString("Description:")))
                .andExpect(content().string(containsString("Date Planted:")));
    }

    @Test
    @WithMockUser
    public void PlantFormSubmitted_ValidNameOnly_PlantAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        String name = "My Plant";
        String description = "";
        String date = "";
        Plant plant = new Plant(name, garden);
        when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                        .file(emptyFile)
                        .param("name", name)
                        .param("count", "")
                        .param("description", description)
                        .param("date", date)
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));

        verify(plantService, times(2)).addPlant(any(Plant.class));
    }

    @Test
    @WithMockUser
    public void PlantFormSubmitted_ValidNameAndCountOnly_PlantAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        String name = "My Plant";
        String count = "2.0";
        Plant plant = new Plant(name, count, garden);
        when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                        .file(emptyFile)
                        .param("name", name)
                        .param("count", count)
                        .param("description", "")
                        .param("date", "")
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));

        verify(plantService, times(2)).addPlant(any(Plant.class));
    }

    @Test
    @WithMockUser
    public void PlantFormSubmitted_ValidNameAndDescriptionOnly_PlantAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        String name = "My Plant";
        String description = "A Plant in My Garden";
        Plant plant = new Plant(name, description, garden);
        when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                        .file(emptyFile)
                        .param("name", name)
                        .param("count", "")
                        .param("description", description)
                        .param("date", "")
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));

        verify(plantService, times(2)).addPlant(any(Plant.class));
    }

    @Test
    @WithMockUser
    public void PlantFormSubmitted_ValidNameAndDateOnly_PlantAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        String name = "My Plant";
        String date = "10/03/2024";
        Plant plant = new Plant(name, garden, date);
        when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                        .file(emptyFile)
                        .param("name", name)
                        .param("count", "")
                        .param("description", "")
                        .param("date", "2024-01-10")
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));

        verify(plantService, times(2)).addPlant(any(Plant.class));
    }

    @Test
    @WithMockUser
    public void PlantFormSubmitted_ValidNameCountAndDescriptionOnly_PlantAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        String name = "My Plant";
        String count = "2";
        String description = "A Plant in My Garden";
        Plant plant = new Plant(name, count, description, garden);
        when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                        .file(emptyFile)
                        .param("name", name)
                        .param("count", "2")
                        .param("description", description)
                        .param("date", "")
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));

        verify(plantService, times(2)).addPlant(any(Plant.class));
    }

    @Test
    @WithMockUser
    public void PlantFormSubmitted_ValidNameCountAndDateOnly_PlantAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        String name = "My Plant";
        String count = "2";
        String date = "10/03/2024";
        Plant plant = new Plant(name, date, garden, count);
        when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                        .file(emptyFile)
                        .param("name", name)
                        .param("count", "2")
                        .param("description", "")
                        .param("date", "2024-03-10")
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));

        verify(plantService, times(2)).addPlant(any(Plant.class));
    }

    @Test
    @WithMockUser
    public void PlantFormSubmitted_ValidNameDescriptionAndDateOnly_PlantAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        String name = "My Plant";
        String description = "A Plant in My Garden";
        String date = "10/03/2024";
        Plant plant = new Plant(name, description, date, garden);
        when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                        .file(emptyFile)
                        .param("name", name)
                        .param("count", "")
                        .param("description", description)
                        .param("date", "2024-01-10")
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));

        verify(plantService, times(2)).addPlant(any(Plant.class));
    }

    @Test
    @WithMockUser
    public void PlantFormSubmitted_AllValidInputs_PlantAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        String name = "My Plant";
        String count = "2";
        String description = "A Plant in My Garden";
        String date = "10/03/2024";
        Plant plant = new Plant(name, count, description, date, garden);
        when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                "image content".getBytes()
        );
        when(imageService.savePlantImage(eq(mockMultipartFile), any(Plant.class))).thenReturn(Optional.empty());

        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                        .file(mockMultipartFile)
                        .param("name", name)
                        .param("count", "2")
                        .param("description", description)
                        .param("date", "2024-01-10")
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));

        verify(plantService, times(1)).addPlant(any(Plant.class));
        verify(imageService, times(1)).savePlantImage(eq(mockMultipartFile), any(Plant.class));
    }

    @Test
    @WithMockUser
    public void PlantFormSubmitted_EmptyName_ErrorMessageAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        String name = "";
        String count = "2";
        String description = "A Plant in My Garden";
        String date = "10/03/2024";
        Plant plant = new Plant(name, count, description, date, garden);
        when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                        .file(emptyFile)
                        .param("name", name)
                        .param("count", "2.0")
                        .param("description", description)
                        .param("date", "2024-03-10")
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("plantsFormTemplate"))
                .andExpect(model().attributeExists("nameError", "name", "count", "description", "date"))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("count", "2.0"))
                .andExpect(model().attribute("description", description))
                .andExpect(model().attribute("date", "2024-03-10"))
                .andExpect(model().attribute("nameError",
                        "Plant name cannot by empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes"));

        verify(plantService, never()).addPlant(any(Plant.class));
    }

    @Test
    @WithMockUser
    public void PlantFormSubmitted_InvalidName_ErrorMessageAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        String name = "~!@#$%^&*()_+";
        String count = "2";
        String description = "A Plant in My Garden";
        String date = "10/03/2024";
        Plant plant = new Plant(name, count, description, date, garden);
        when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                        .file(emptyFile)
                        .param("name", name)
                        .param("count", "2.0")
                        .param("description", description)
                        .param("date", "2024-03-10")
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("plantsFormTemplate"))
                .andExpect(model().attributeExists("nameError", "name", "count", "description", "date"))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("count", "2.0"))
                .andExpect(model().attribute("description", description))
                .andExpect(model().attribute("date", "2024-03-10"))
                .andExpect(model().attribute("nameError",
                        "Plant name cannot by empty and must only include letters, numbers, spaces, dots, hyphens or apostrophes"));

        verify(plantService, never()).addPlant(any(Plant.class));
    }

    @Test
    @WithMockUser
    public void PlantFormSubmitted_CountNotANumber_ErrorMessageAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        String name = "My Plant";
        String count = "2";
        String description = "A Plant in My Garden";
        String date = "10/03/2024";
        Plant plant = new Plant(name, count, description, date, garden);
        when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                        .file(emptyFile)
                        .param("name", name)
                        .param("count", "Not a Number")
                        .param("description", description)
                        .param("date", "2024-03-10")
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("plantsFormTemplate"))
                .andExpect(model().attributeExists("countError", "name", "count", "description", "date"))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("count", "Not a Number"))
                .andExpect(model().attribute("description", description))
                .andExpect(model().attribute("date", "2024-03-10"))
                .andExpect(model().attribute("countError",
                        "Plant count must be a positive number"));

        verify(plantService, never()).addPlant(any(Plant.class));
    }

    @Test
    @WithMockUser
    public void PlantFormSubmitted_NegativeCount_ErrorMessageAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        String name = "My Plant";
        String count = "2";
        String description = "A Plant in My Garden";
        String date = "10/03/2024";
        Plant plant = new Plant(name, count, description, date, garden);
        when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                        .file(emptyFile)
                        .param("name", name)
                        .param("count", "-2.0")
                        .param("description", description)
                        .param("date", "2024-03-10")
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("plantsFormTemplate"))
                .andExpect(model().attributeExists("countError", "name", "count", "description", "date"))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("count", "-2.0"))
                .andExpect(model().attribute("description", description))
                .andExpect(model().attribute("date", "2024-03-10"))
                .andExpect(model().attribute("countError",
                        "Plant count must be a positive number"));

        verify(plantService, never()).addPlant(any(Plant.class));
    }

    @Test
    @WithMockUser
    public void PlantFormSubmitted_DescriptionOverLimit_ErrorMessageAddedAndViewUpdated() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(garden));

        String name = "My Plant";
        String count = "2";
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
        String date = "10/03/2024";
        Plant plant = new Plant(name, count, description, date, garden);
        when(plantService.addPlant(any(Plant.class))).thenReturn(plant);

        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/gardens/details/plants/form")
                        .file(emptyFile)
                        .param("name", name)
                        .param("count", "2.0")
                        .param("description", description)
                        .param("date", "2024-03-10")
                        .param("gardenId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("plantsFormTemplate"))
                .andExpect(model().attributeExists("descriptionError", "name", "count", "description", "date"))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("count", "2.0"))
                .andExpect(model().attribute("description", description))
                .andExpect(model().attribute("date", "2024-03-10"))
                .andExpect(model().attribute("descriptionError",
                        "Plant description must be less than 512 characters"));

        verify(plantService, never()).addPlant(any(Plant.class));
    }

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
        Garden garden = new Garden("My Garden", "Ilam");
        Plant plant = new Plant("My Plant", "2", "Rose", "12/06/2004", garden);
        String plantId = "2";
        List<Garden> gardens = new ArrayList<>();
        when(gardenService.getGardenResults()).thenReturn(gardens);
        when(plantService.getPlant(Long.parseLong(plantId))).thenReturn(Optional.of(plant));

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
        Garden garden = new Garden("My Garden", "Ilam");
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
        Garden garden = new Garden("My Garden", "Ilam");
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

    @Test
    @WithMockUser
    public void ImageUploaded_ValidImage_PlantImageUpdated() throws Exception {
        Garden garden = new Garden("My Garden", "Ilam");
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
        Garden garden = new Garden("My Garden", "Ilam");
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
