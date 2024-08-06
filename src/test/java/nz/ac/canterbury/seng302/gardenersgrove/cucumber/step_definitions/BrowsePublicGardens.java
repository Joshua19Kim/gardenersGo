package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.And;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ForgotPasswordFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class BrowsePublicGardens {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GardenRepository gardenRepository;

    @Autowired
    private GardenService gardenService;
    private ResultActions resultActions;
    private String searchTerm;
    private Gardener gardener;

    private List<String> allTags = new ArrayList<>();


    @Autowired
    private GardenerFormService gardenerFormService;


    @Given("there is a garden with the name {string}")
    public void thereIsAGardenWithTheName(String gardenName) {
        gardener = gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        Garden garden = new Garden(gardenName, "99 test address", null, "Christchurch", "New Zealand", null, "9999", gardener, "");
        garden.setIsGardenPublic(true);
        gardenService.addGarden(garden);
    }


    @Given("I am on the browse gardens page to search")
    public void iAmOnTheBrowseGardensPageToSearch() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/browseGardens"))
                .andExpect(view().name("browseGardensTemplate"))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Given("I input the search term {string}")
    public void iInputTheSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @When("I press the search button")
    public void iPressTheSearchButton() throws Exception {
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/browseGardens")
                .param("searchTerm", searchTerm)
                .with(csrf()));
    }

    @Then("The gardens with matching results are shown")
    public void theGardensWithMatchingResultsAreShown() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("browseGardensTemplate"));

        MvcResult mvcResult = resultActions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("Apple Orchard"), "Garden 'Apple Orchard' not found in search results");
    }

}
