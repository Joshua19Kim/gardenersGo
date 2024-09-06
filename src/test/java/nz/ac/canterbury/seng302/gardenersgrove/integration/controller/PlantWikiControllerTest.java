package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import nz.ac.canterbury.seng302.gardenersgrove.controller.PlantWikiController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlant;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.mock.web.MockMultipartFile;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers= PlantWikiController.class)
public class PlantWikiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlantWikiService plantWikiService;

    @MockBean
    private PlantService plantService;

    @MockBean
    private GardenerFormService gardenerFormService;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private ImageService  imageService;

    private List<WikiPlant> expectedWikiPlants;
    private List<WikiPlant> expectedSearchWikiPlants;

    private Plant plant;

    private Gardener testGardener;

    @BeforeEach
    public void setUp() {
        int totalWikiPlants = 10;
        int totalSearchWikiPlants = 3;
        expectedWikiPlants = new ArrayList<>();
        expectedSearchWikiPlants = new ArrayList<>();

        testGardener = new Gardener("Test", "Gardener", LocalDate.of(2024, 4, 1), "testgardener@gmail.com", "Password1!");

        // Mock gardener retrieval by email (authentication)
        Mockito.when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(testGardener));

        for(int i = 0; i < totalWikiPlants; i++) {
            WikiPlant expectedWikiPlant;
            if(i >= totalWikiPlants - totalSearchWikiPlants) {
                expectedWikiPlant = new WikiPlant((long) i, "Pine tree" + i, List.of("Pine"), List.of("Common Silver Fir"), "Perennial", "Frequent", List.of("full sun"), "randomImage.jpeg");
                expectedSearchWikiPlants.add(expectedWikiPlant);
            } else {
                expectedWikiPlant = new WikiPlant((long) i, "European Silver Fir " + i, List.of("Abies alba"), List.of("Common Silver Fir"), "Perennial", "Frequent", List.of("full sun"), "randomImage.jpeg");
            }
            expectedWikiPlants.add(expectedWikiPlant);
        }
    }


    @Test
    @WithMockUser
    public void PlantWikiPageRequested_ValidRequest_PlantWikiPageReturned() throws Exception{
        String query = "";
        Mockito.when(plantWikiService.getPlants(query)).thenReturn(expectedWikiPlants);
        mockMvc.perform(MockMvcRequestBuilders.get("/plantWiki"))
                .andExpect(view().name("plantWikiTemplate"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("resultPlants", expectedWikiPlants));
    }

    @Test
    @WithMockUser
    public void PlantWikiPageSearched_ValidSearch_PlantWikiPageReturnedWithSearchResults() throws Exception{
        String query = "Pine";
    Mockito.when(plantWikiService.getPlants(query)).thenReturn(expectedSearchWikiPlants);
        mockMvc.perform(MockMvcRequestBuilders.post("/plantWiki")
                        .param("searchTerm", query)
                        .with(csrf()))
                .andExpect(view().name("plantWikiTemplate"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("resultPlants", expectedSearchWikiPlants))
                .andExpect(model().attribute("searchTerm", query));
    }

    @Test
    @WithMockUser
    public void PlantWikiPageSearched_NoResults_PlantWikiPageReturnedWithNoResultsAndMessage() throws Exception{
        String query = "Hello World";
        String errorMessage = "No plants were found";
    Mockito.when(plantWikiService.getPlants(query)).thenReturn(new ArrayList<>());
    mockMvc
        .perform(MockMvcRequestBuilders.post("/plantWiki").param("searchTerm", query).with(csrf()))
        .andExpect(view().name("plantWikiTemplate"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("resultPlants", new ArrayList<>()))
        .andExpect(model().attribute("searchTerm", query))
        .andExpect(model().attribute("errorMessage", errorMessage));
    }

  @Test
  @WithMockUser
  public void PlantWikiPageSearched_APIDown_PlantWikiPageReturnedWithErrorMessage()
      throws Exception {
    String query = "Pine";
    String errorMessage = "The plant wiki is down for the day :( Try again tomorrow";
    Mockito.when(plantWikiService.getPlants(query)).thenReturn(errorMessage);

    mockMvc
        .perform(MockMvcRequestBuilders.post("/plantWiki").param("searchTerm", query).with(csrf()))
        .andExpect(view().name("plantWikiTemplate"))
        .andExpect(status().isOk())
        .andExpect(model().attribute("resultPlants", new ArrayList<>())) // Expecting empty list
        .andExpect(model().attribute("searchTerm", query))
        .andExpect(
            model()
                .attribute("errorMessage", errorMessage)); // Expecting error message in the model
  }
    @Test
    @WithMockUser
    public void PlantWikiPlantAdded_ValidRequest_PlantAddedToGarden() throws Exception {
        Garden mockGarden = Mockito.spy(Garden.class);
        Long gardenId = 1L;
        mockGarden.setId(gardenId);

        String name = "Apple Tree";
        String count = "2";
        String description = "Big ol' fruit tree";
        String date = "2020-01-01";

        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "Some image content".getBytes());
        Mockito.when(gardenService.getGarden(gardenId)).thenReturn(Optional.of(mockGarden));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/addPlant")
                        .file(file)
                        .param("gardenId", String.valueOf(gardenId))
                        .param("name", name)
                        .param("count", count)
                        .param("description", description)
                        .param("date", date)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/plantWiki"));
    }

    @Test
    @WithMockUser
    public void addPlant_InvalidPlantName_ReturnsError() throws Exception {
        Garden mockGarden = Mockito.spy(Garden.class);
        Long gardenId = 1L;
        Mockito.when(gardenService.getGarden(gardenId)).thenReturn(Optional.of(mockGarden));

        mockMvc.perform(MockMvcRequestBuilders.post("/addPlant")
                        .param("gardenId", String.valueOf(gardenId))
                        .param("name", "")
                        .param("count", "1")
                        .param("description", "A test plant")
                        .param("date", "2023-01-01")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("nameError"))
                .andExpect(redirectedUrl("/plantWiki"));
    }

    @Test
    @WithMockUser
    public void addPlant_InvalidCount_ReturnsError() throws Exception {
        Garden mockGarden = Mockito.spy(Garden.class);
        Long gardenId = 1L;
        Mockito.when(gardenService.getGarden(gardenId)).thenReturn(Optional.of(mockGarden));

        mockMvc.perform(MockMvcRequestBuilders.post("/addPlant")
                        .param("gardenId", String.valueOf(gardenId))
                        .param("name", "Test Plant")
                        .param("count", "invalid")
                        .param("description", "A test plant")
                        .param("date", "2023-01-01")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("countError"))
                .andExpect(redirectedUrl("/plantWiki"));
    }

    @Test
    @WithMockUser
    public void addPlant_InvalidDate_ReturnsError() throws Exception {
        Garden mockGarden = Mockito.spy(Garden.class);
        Long gardenId = 1L;
        Mockito.when(gardenService.getGarden(gardenId)).thenReturn(Optional.of(mockGarden));

        mockMvc.perform(MockMvcRequestBuilders.post("/addPlant")
                        .param("gardenId", String.valueOf(gardenId))
                        .param("name", "Test Plant")
                        .param("count", "1")
                        .param("description", "A test plant")
                        .param("date", "invalid-date")
                        .param("isDateInvalid", "true")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("dateError"))
                .andExpect(redirectedUrl("/plantWiki"));
    }

    @Test
    @WithMockUser
    public void addPlant_MissingFileAndUrl_UsesPlaceholderImage() throws Exception {
        Garden mockGarden = Mockito.spy(Garden.class);
        Long gardenId = 1L;
        Mockito.when(gardenService.getGarden(gardenId)).thenReturn(Optional.of(mockGarden));

        mockMvc.perform(MockMvcRequestBuilders.post("/addPlant")
                        .param("gardenId", String.valueOf(gardenId))
                        .param("name", "Test Plant")
                        .param("count", "1")
                        .param("description", "A test plant")
                        .param("date", "2023-01-01")
                        .param("imageUrl", "")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(redirectedUrl("/plantWiki"));

        Mockito.verify(plantService, Mockito.times(2)).addPlant(Mockito.argThat(plant -> plant.getImage().equals("/images/placeholder.jpg")));
    }
}