package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import nz.ac.canterbury.seng302.gardenersgrove.controller.PlantWikiController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.WikiPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantWikiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers= PlantWikiController.class)
public class PlantWikiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlantWikiService plantWikiService;

    private List<WikiPlant> expectedWikiPlants;
    private List<WikiPlant> expectedSearchWikiPlants;

    @BeforeEach
    public void setUp() {
        int totalWikiPlants = 10;
        int totalSearchWikiPlants = 3;
        expectedWikiPlants = new ArrayList<>();
        expectedSearchWikiPlants = new ArrayList<>();
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
}