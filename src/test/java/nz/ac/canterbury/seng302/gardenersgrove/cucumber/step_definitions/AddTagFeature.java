package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class AddTagFeature {

  @Autowired private MockMvc mockMvcGardenDetailsController;
  @Autowired private TagService tagService;
  @Autowired private GardenerFormService gardenerFormService;
  @Autowired private GardenService gardenService;
  private Garden garden;
  private Tag tag;
  private Gardener gardener;
  private MvcResult mvcResult;

  @Before("@U21")
  public void setUp() {
    Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail("a@gmail.com");
    gardener = gardenerOptional.get();
    Optional<Garden> gardenOptional = gardenService.getGarden(1L);
    garden = gardenOptional.get();
  }

  @Given("I add a valid tag")
  public void i_add_a_valid_tag() {
    tag = new Tag("My tag", garden);

  }
  @Given("I add an invalid {string}")
  public void i_add_an_invalid(String tagInput) {
    tag = new Tag(tagInput, garden);
  }

  @When("I confirm the tag name")
  public void i_confirm_the_tag_name() throws Exception {
    mvcResult= mockMvcGardenDetailsController
            .perform(
                    (MockMvcRequestBuilders.post("/gardens/addTag")
                            .param("tag-input", tag.getName())
                            .param("gardenId", "1")
                            .with(csrf())))
            .andReturn();
  }

  @Then("the tag is not added to the garden tags")
  public void the_tag_is_not_added_to_the_garden_tags() {
    assertEquals(tagService.findTagByNameAndGarden(tag.getName(), garden), Optional.empty());
  }

  @Then("the tag is added to the garden tags")
  public void the_tag_is_added_to_the_garden_tags() {
    // Write code here that turns the phrase above into concrete actions
    assertEquals(tagService.findTagByNameAndGarden(tag.getName(), garden).get().getName(), tag.getName());
  }

  @Then("an error message displays {string}")
  public void an_error_message_says(String errorMessage) {
    assertEquals(errorMessage, Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("tagValid"));
  }
}
