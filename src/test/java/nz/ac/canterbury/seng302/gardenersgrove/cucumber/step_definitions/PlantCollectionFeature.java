package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.IdentifiedPlantService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class PlantCollectionFeature {
    @Autowired private MockMvc mockMvc;
    @Autowired private GardenerFormService gardenerFormService;
    @Autowired private IdentifiedPlantService identifiedPlantService;
    private MvcResult mvcResult;
    private Long gardenerId;

    private final Logger logger = LoggerFactory.getLogger(PlantCollectionFeature.class);

    @Before("@U7001 or @7002")
    public void setUp() {
      gardenerFormService.findByEmail("a@gmail.com").ifPresent(gardener -> gardenerId = gardener.getId());
    }

    @When("I click on an entry for a plant on my collections page")
    public void i_click_on_an_entry_for_a_plant() throws Exception {
        mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/collectionDetails").param("speciesName", "Cynara scolymus")).andReturn();
    }

    @Then("I see a list of all the unique plants that I have collected before that belong to that plant species")
    public void i_see_a_list_of_all_the_unique_plants() throws Exception {
        Page<IdentifiedPlant> plants = identifiedPlantService.getGardenerPlantsBySpeciesPaginated(0, 12, gardenerId, "Cynara scolymus");
        assertEquals("collectionDetailsTemplate", Objects.requireNonNull(mvcResult.getModelAndView()).getViewName());
        assertEquals(mvcResult.getModelAndView().getModel().get("collectionsList").toString(), plants.toString());
    }
}