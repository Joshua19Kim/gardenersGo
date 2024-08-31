package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.PlantWikiController;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        testGardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        for(int i = 0; i < totalWikiPlants; i++) {
            WikiPlant expectedWikiPlant;
            if(i >= totalWikiPlants - totalSearchWikiPlants) {
                expectedWikiPlant = new WikiPlant((long) i, "Pine tree" + i, List.of("Pine"), List.of("Common Silver Fir"),
                        "Perennial", "Frequent", List.of("full sun"), "randomImage.jpeg");
                expectedSearchWikiPlants.add(expectedWikiPlant);
            } else {
                expectedWikiPlant = new WikiPlant((long) i, "European Silver Fir " + i, List.of("Abies alba"), List.of("Common Silver Fir"),
                        "Perennial", "Frequent", List.of("full sun"), "randomImage.jpeg");
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
        List<WikiPlant> emptyList = new ArrayList<>();
        Mockito.when(plantWikiService.getPlants(query)).thenReturn(emptyList);
        mockMvc.perform(MockMvcRequestBuilders.post("/plantWiki")
                        .param("searchTerm", query)
                        .with(csrf()))
                .andExpect(view().name("plantWikiTemplate"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("resultPlants", emptyList))
                .andExpect(model().attribute("searchTerm", query))
                .andExpect(model().attribute("errorMessage", errorMessage));
    }

    @Test
    @WithMockUser
    public void PlantWikiPlantAdded_ValidRequest_PlantAddedToGarden() throws Exception{
        String query = "";
        Long gardenId = 1L;
        String name = "Apple Tree";
        String count = "2";
        String description = "Big ol' fruit tree";
        String date = "01/01/2020";
        Mockito.when(plantWikiService.getPlants(query)).thenReturn(expectedWikiPlants);
        mockMvc.perform(MockMvcRequestBuilders.post("/addPlant")
                        .param("gardenId", String.valueOf(gardenId))
                        .param("name", name)
                        .param("count", count)
                        .param("description", description)
                        .param("date", date)
                .with(csrf()))
                .andExpect(view().name("plantWikiTemplate"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("gardenId", gardenId))
                .andExpect(model().attribute("name", name))
                .andExpect(model().attribute("count", count))
                .andExpect(model().attribute("description", description))
                .andExpect(model().attribute("date", date));
    }
}
