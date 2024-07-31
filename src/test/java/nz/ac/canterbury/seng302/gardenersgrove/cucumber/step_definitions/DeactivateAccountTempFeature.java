package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;import org.springframework.beans.factory.annotation.Autowired;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class DeactivateAccountTempFeature {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TagService tagService;
    @Autowired
    private GardenerFormService gardenerFormService;
    @Autowired
    private GardenService gardenService;
    private Gardener gardener;
    private Garden garden;
    private Tag tag;
    private ResultActions resultActions;

    @Before("@U23")
    public void setUp() {
        Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail("a@gmail.com");
        gardener = gardenerOptional.get();
        Optional<Garden> gardenOptional = gardenService.getGarden(1);
        garden = gardenOptional.get();
    }

    @Given("I have added inappropriate words four times and am adding one more time")
    public void i_have_added_inappropriate_words_four_times_and_am_adding_one_more_time() {
        gardener.setBadWordCount(4);
        gardenerFormService.addGardener(gardener);
        tag = new Tag("Fuck", garden);
    }
    @When("I try to submit the tag")
    public void i_try_to_submit_the_tag() throws Exception {
        resultActions = mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/addTag")
                                .param("tag-input", tag.getName())
                                .param("gardenId", "1")
                                .with(csrf())));
    }
    @Then("the system shows the warning message and send an warning email.")
    public void the_system_shows_the_warning_message_and_send_an_warning_email() throws Exception {
        resultActions.andExpect(model().attribute("tagWarning", "You have added an inappropriate tag for the fifth time"));
    }
}
