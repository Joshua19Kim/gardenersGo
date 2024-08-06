package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class MainPageFeature {

  @Autowired private MockMvc mockMvcLogin;

  @Autowired private GardenRepository gardenRepository;

  @Autowired private GardenerFormRepository gardenerFormRepository;

  @Autowired private PlantRepository plantRepository;

  @Autowired private LostPasswordTokenRepository lostPasswordTokenRepository;

  @Autowired private GardenService gardenService;

  @Autowired private GardenerFormService gardenerFormService;

  @Autowired private PlantService plantService;

  private Gardener gardener;
  private Garden garden;
  private ResultActions resultActions;

  @Before("@U25")
  public void setup() {
    plantRepository.deleteAll();
    gardenRepository.deleteAll();
    gardenerFormRepository.deleteAll();
    lostPasswordTokenRepository.deleteAll();
  }

  @Given("I am a valid user")
  public void i_am_a_valid_user() {
    gardener = new Gardener("John", "Doe", LocalDate.of(2000, 1, 1), "tester@gmail.com", "Password1!");
    System.out.println("User created or retrieved: " + gardener);
  }

  @When("I submit the login form")
  public void i_submit_the_login_form() throws Exception {
    resultActions =
            mockMvcLogin.perform(
                    MockMvcRequestBuilders.post("/login")
                            .param("username", "tester@gmail.com")
                            .param("password", "Password1!")
                            .with(csrf()));
  }

  @Then("I am taken by default to the home page")
  public void i_am_taken_by_default_to_the_home_page() throws Exception {
    MvcResult mvcResult = resultActions.andExpect(status().is3xxRedirection()).andReturn();
    assertEquals("/home", mvcResult.getResponse().getHeader("Location"));
    // Print actual redirection location
    String actualLocation = mvcResult.getResponse().getHeader("Location");
    System.out.println("Redirect location: " + actualLocation);

    assertEquals("/home", actualLocation);
  }


  @Given("I am a valid user with gardens with plants")
  public void i_am_a_valid_user_with_gardens_with_plants() throws Exception {
    i_am_a_valid_user();

    garden =
        new Garden("Garden 1", "Location 1", null, "City", "Country", null, "12345", gardener, "");
    gardenService.addGarden(garden);

    List<Plant> plants =
        Arrays.asList(
            new Plant("Plant 1", garden),
            new Plant("Plant 2", garden),
            new Plant("Plant 3", garden));

    for (Plant plant : plants) {
      plantService.addPlant(plant);
    }
  }

  @Given("I am a valid user with gardens with no plants")
  public void i_am_a_valid_user_with_gardens_with_no_plants() throws Exception {
    i_am_a_valid_user();
    garden =
        new Garden("Garden 1", "Location 1", null, "City", "Country", null, "12345", gardener, "");
    gardenService.addGarden(garden); // Save garden to the database
  }

  @Then("the newest plant widget displays my three newest plants")
  public void the_newest_plant_widget_displays_my_three_newest_plants() throws Exception {
    resultActions = mockMvcLogin.perform(MockMvcRequestBuilders.get("/home").with(csrf()));

    MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    Assertions.assertTrue(content.contains("Plant 1"));
    Assertions.assertTrue(content.contains("Plant 2"));
    Assertions.assertTrue(content.contains("Plant 3"));
  }

  @Then("the newest plant widget displays error message")
  public void the_newest_plant_widget_displays_error_message() throws Exception {
    resultActions = mockMvcLogin.perform(MockMvcRequestBuilders.get("/home").with(csrf()));

    MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    Assertions.assertTrue(content.contains("No plants added"));
  }
}
