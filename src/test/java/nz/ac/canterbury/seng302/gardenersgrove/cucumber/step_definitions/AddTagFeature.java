package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Optional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class AddTagFeature {

  @Autowired private MockMvc mockMvcGardenDetailsController;
  @Autowired private TagService tagService;
  @Autowired private GardenerFormService gardenerFormService;
  @Autowired private GardenService gardenService;
  private Garden garden;
  private Tag tag;
  private Gardener gardener;

  @Before("@U21")
  public void setUp() {
    Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail("a@gmail.com");
    gardener = gardenerOptional.get();
    Optional<Garden> gardenOptional = gardenService.getGarden(1L);
    garden = gardenOptional.get();
  }

  @Given("I add an invalid tag")
  public void i_add_an_invalid_tag() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("the tag is not added the garden tags")
  public void the_tag_is_not_added_the_garden_tags() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("the tag is added to the garden tags")
  public void the_tag_is_added_to_the_garden_tags() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("an error message displays {string}")
  public void an_error_message_says(String errorMessage) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }
}
