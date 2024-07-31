package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.WordFilter;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class TagModerationFeature {

    @Autowired
    private MockMvc mockMvcGardenDetailsController;
    @Autowired
    private TagService tagService;
    @Autowired
    private GardenerFormService gardenerFormService;
    @Autowired
    private GardenService gardenService;
    @Autowired
    private WriteEmail writeEmail;
    private Garden garden;
    private Tag tag;
    private Gardener gardener;

    @Before("@U22")
    public void setUp() {
        Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail("a@gmail.com");
        gardener = gardenerOptional.get();
        Optional<Garden> gardenOptional = gardenService.getGarden(1);
        garden = gardenOptional.get();
    }

    @Given("I am adding a valid tag")
    public void i_am_adding_a_valid_tag() {
        tag = new Tag("My tag", garden);

    }

    @Given("I add an inappropriate tag")
    public void the_submitted_tag_is_evaluated_for_appropriateness() {
        tag = new Tag("Fuck", garden);
    }

    @When("I confirm the tag")
    public void i_confirm_the_tag() throws Exception {
        mockMvcGardenDetailsController
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/addTag")
                                .param("tag-input", tag.getName())
                                .param("gardenId", "1")
                                .with(csrf())))
                .andReturn();
    }
    @Then("The tag is checked for offensive or inappropriate words")
    public void the_tag_is_checked_for_offensive_or_inappropriate_words() {
        assertEquals(false, WordFilter.doesContainBadWords(tag.getName()));
    }

    @Then("the tag is not added to the list of user-defined tags")
    public void the_tag_is_not_added_to_the_list_of_user_defined_tags() {
        assertEquals(tagService.findTagByNameAndGarden("Fuck", garden), Optional.empty());
    }

    @Then("the users bad word counter is incremented by one")
    public void the_users_bad_word_counter_is_incremented_by_one() {
        gardenerFormService.addGardener(gardener);
        assertEquals(1, gardener.getBadWordCount());
    }
}
