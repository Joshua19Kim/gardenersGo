package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class DeactivateAccountTempFeature {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GardenerFormService gardenerFormService;
    @Autowired
    private GardenService gardenService;
    @Autowired
    private GardenerFormRepository gardenerFormRepository;
    private Gardener gardener;
    private Garden garden;
    private Tag tag;
    private ResultActions resultActions;

    @Before("@U23")
    public void setUp() {
        Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail("b@gmail.com");
        gardenerOptional.ifPresent(value -> gardener = value);
        Optional<Garden> gardenOptional = gardenService.getGarden(1);
        gardenOptional.ifPresent(value -> garden = value);
    }

    @AfterEach
    public void tearDown() {
        gardenerFormRepository.deleteAll();
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
    @Then("the system shows the warning message")
    public void the_system_shows_the_warning_message() throws Exception {
        resultActions.andExpect(model().attribute("tagWarning", "You have added an inappropriate tag for the fifth time. If you add one more, your account will be blocked for one week."));
    }

    @Given("I have added {int} inappropriate tags")
    public void i_have_added_inappropriate_tags(int badWordCount) {
        gardener.setBadWordCount(badWordCount);
        gardenerFormService.addGardener(gardener);
        tag = new Tag("Fuck", garden);
    }

    @When("I add another inappropriate tag")
    public void i_add_another_inappropriate_tag() throws Exception {
        resultActions = mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/addTag")
                                .param("tag-input", tag.getName())
                                .param("gardenId", "1")
                                .with(csrf())));
    }

    @Then("I am unlogged from the system")
    public void i_am_unlogged_from_the_system() throws Exception {
        resultActions.andExpect(redirectedUrl("/login?banned"));
    }

    @Given("I am banned")
    public void i_am_banned() {
        gardener.banGardener();
        gardenerFormService.addGardener(gardener);
    }

    @When("I try to log in with email {string} and password {string}")
    public void i_try_to_log_in_with_email_test_and_password(String email, String password) throws Exception {
        resultActions = mockMvc
                .perform(
                        (MockMvcRequestBuilders.post("/login")
                                .param("username", email)
                                .param("password", password)
                                .with(csrf())));
    }

    @Then("I am not logged in")
    public void i_am_not_logged_in() throws Exception {
        resultActions.andExpect(redirectedUrl("/login?error"));
    }
}
