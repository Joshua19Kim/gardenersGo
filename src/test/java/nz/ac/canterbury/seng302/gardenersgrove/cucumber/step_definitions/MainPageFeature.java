package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenVisitService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import nz.ac.canterbury.seng302.gardenersgrove.service.RelationshipService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class MainPageFeature {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private GardenerFormRepository gardenerFormRepository;
  @Autowired
  private PlantService plantService;
  @Autowired
  private GardenService gardenService;
  private Gardener gardener;
  private ResultActions resultActions;
  List<Plant> newestPlants;


  @Given("I am a valid user")
  public void i_am_a_valid_user() {
    gardener = new Gardener("John", "Doe", LocalDate.of(2000, 1, 1), "a@gmail.com", "Password1!");
  }

  @When("I submit the login form")
  public void i_submit_the_login_form() throws Exception {
    resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .param("username", gardener.getEmail())
            .param("password", "Password1!")
            .with(csrf()));
  }

  @Then("I am taken by default to the home page")
  public void i_am_taken_by_default_to_the_home_page() throws Exception {
    resultActions
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/home"));


  }

  @Then("I can see the empty widget of newest plants")
  public void i_can_see_the_empty_widget_of_newest_plants() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/home")
                    .with(SecurityMockMvcRequestPostProcessors.user(gardener.getEmail())))
            .andExpect(status().isOk())
            .andExpect(view().name("mainPageTemplate"))
            .andExpect(model().attributeExists("newestPlants"))
            .andExpect(model().attribute("newestPlants",Matchers.empty()));

  }

  @When("I have a garden and three plants")
  public void i_have_a_garden_and_three_plants() {
    Optional<Gardener> gardenerOptional = gardenerFormRepository.findByEmail("a@gmail.com");

    Garden testGarden = new Garden("Botanical",
            "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", gardenerOptional.get(), "");
    gardenService.addGarden(testGarden);

    Plant oldPlant = new Plant("Old Plant", testGarden);
    Plant middlePlant = new Plant("Middle Plant", testGarden);
    Plant youngPlant = new Plant("Young Plant", testGarden);
    plantService.addPlant(oldPlant);
    plantService.addPlant(middlePlant);
    plantService.addPlant(youngPlant);
    newestPlants = new ArrayList<>();
    newestPlants.add(oldPlant);
    newestPlants.add(middlePlant);
    newestPlants.add(youngPlant);



  }
  @Then("I can see the newest plant widget with my three newest plants")
  public void i_can_see_the_newest_plant_widget_with_my_three_newest_plants() throws Exception {
    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/home")
                    .with(SecurityMockMvcRequestPostProcessors.user(gardener.getEmail())))
            .andExpect(status().isOk())
            .andExpect(view().name("mainPageTemplate"))
            .andExpect(model().attributeExists("newestPlants"))
            .andReturn();

    List<Plant> returnedPlants = (List<Plant>) mvcResult.getModelAndView().getModel().get("newestPlants");
    List<Plant> reversedExpectedPlants = newestPlants.reversed();

    for (int i = 0; i < returnedPlants.size(); i++) {
      Assertions.assertEquals(returnedPlants.get(i).getName(),reversedExpectedPlants.get(i).getName());

    }
  }

}
