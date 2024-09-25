package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.BadgeService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.IdentifiedPlantService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ScanningPlantFeature {
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private IdentifiedPlantService identifiedPlantService;
  @Autowired
  private GardenerFormService gardenerFormService;
  @Autowired
  private BadgeService badgeService;
  private MockMultipartFile imageFile;
  private String errorMessage;
  private String inputName;
  private String inputDescription;
  private String inputLatitude;
  private String inputLongitude;
  private ResultActions resultActions;
  private IdentifiedPlant cataloguedPlant;

    @Before("@U7001 or @7002 or @7004")
  public void setUp() {
        Gardener currentUser = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
    gardenerFormService.addGardener(currentUser);
  }

  @Given("I have an image of a plant")
  public void i_have_an_image_of_a_plant() {
    byte[] imageContent = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
    imageFile = new MockMultipartFile("image", "test_image.jpg", "image/jpeg", imageContent);
  }

  @Then("the app displays the name and relevant details after identifying")
  public void the_app_displays_the_name_and_relevant_details_after_identifying() throws Exception {
    // Mock testIdentifiedPlant(contains mock plant details) has been defined in
    // MockConfigurationSteps
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/identifyPlant").file(imageFile).with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bestMatch").value("Helianthus annuus"))
        .andExpect(jsonPath("$.score").value(0.88))
        .andExpect(jsonPath("$.gbifId").value("5414641"))
        .andExpect(jsonPath("$.imageUrl").value("https://example.com/sunflower.jpg"));
  }

  @Given("I uploaded an blurry image")
  public void i_uploaded_an_blurry_image() {
    byte[] imageContent = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
    imageFile = new MockMultipartFile("image", "test_image.jpg", "image/jpeg", imageContent);
    errorMessage =
        "Please ensure the plant is taking up most of the frame and the photo is not blurry.";
  }

  @Given("I uploaded an non_plant image")
  public void i_uploaded_an_non_plant_image() {
    byte[] imageContent = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
    imageFile = new MockMultipartFile("image", "test_image.jpg", "image/jpeg", imageContent);
    errorMessage =
        "There is no matching plant with your image. Please try with a different image of the plant.";
  }

  @Given("I have a catalogued plant")
  public void i_have_a_catalogued_plant() {
    Gardener plantOwner = new Gardener("Test", "Gardener",
            LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
            "Password1!");
    gardenerFormService.addGardener(plantOwner);

    cataloguedPlant = new IdentifiedPlant(
            "Helianthus annuus",
            0.88,
            List.of("Sunflower", "Rose"),
            "5414641",
            "https://example.com/sunflower.jpg",
            "https://example.com/sunflower.jpg",
            "Helianthus",
            "annuus",
            plantOwner
    );
    identifiedPlantService.saveIdentifiedPlantDetails(cataloguedPlant);
  }

  @When("I click the edit button")
  public void i_click_the_edit_button() throws Exception {
    resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/collectionDetails/edit")
            .param("plantId", cataloguedPlant.getId().toString())
            .with(csrf()));
  }

  @Then("I see a form for editing the plant")
  public void i_see_a_form_for_editing_the_plant() throws Exception {
    resultActions
            .andExpect(status().isOk())
            .andExpect(view().name("editIdentifiedPlantForm"))
            .andExpect(model().attributeExists("plant"));
    MvcResult mvcResult = resultActions.andReturn();
    IdentifiedPlant plant = (IdentifiedPlant) Objects.requireNonNull(mvcResult.getModelAndView()).getModel().get("plant");
    assertEquals(plant.getName(), cataloguedPlant.getName());
  }

  @Given("I am on the edit plant form for the catalogued plant")
  public void i_am_on_the_edit_plant_form_for_the_catalogued_plant() {
    cataloguedPlant.setName("OldName");
    cataloguedPlant.setDescription("Old description");
  }

  @When("I submit the edit plant form")
  public void i_submit_the_edit_plant_form() throws Exception {
    resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/collectionDetails/edit")
            .param("name",inputName)
            .param("description", inputDescription)
            .param("plantId", cataloguedPlant.getId().toString())
                    .param("manualPlantLat", inputLatitude)
                    .param("manualPlantLon", inputLongitude)
            .with(csrf()));
  }

  @Then("the information is updated")
  public void the_information_is_updated() {
    IdentifiedPlant plant = identifiedPlantService.getCollectionPlantById(cataloguedPlant.getId());
    assertEquals(inputName, plant.getName());
    assertEquals((inputDescription.isEmpty() ? null : inputDescription), plant.getDescription());
    assertEquals(inputLatitude, plant.getPlantLatitude());
    assertEquals(inputLongitude, plant.getPlantLongitude());
  }

  @Then("I get the error message {string}")
  public void i_get_the_error_message(String message) throws Exception {
    resultActions
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("plant"))
            .andExpect(view().name("editIdentifiedPlantForm"));

    Map<String, Object> result = Objects.requireNonNull(resultActions.andReturn().getModelAndView()).getModel();

    // NB: at present this only works with testing one error at a time
    if (result.containsKey("nameError")) {
      assertEquals(message, result.get("nameError"));
    }
    if (result.containsKey("descriptionError")) {
      assertEquals(message, result.get("descriptionError"));
    }
    if(result.containsKey("locationError")) {
      assertEquals(message, result.get("locationError"));
    }
  }

  @Then("I should be informed that the app failed to identify plant")
  public void i_should_be_informed_that_the_app_failed_to_identify_plant() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.multipart("/identifyPlant").file(imageFile).with(csrf()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").value(errorMessage));
  }

  @Then("my plant is saved")
  public void my_plant_is_saved() throws Exception {
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("name", inputName);
    requestBody.put("description", inputDescription);
    requestBody.put("plantLatitude", inputLatitude);
    requestBody.put("plantLongitude", inputLongitude);

    ObjectMapper objectMapper = new ObjectMapper();
    String jsonBody = objectMapper.writeValueAsString(requestBody);

    resultActions =
        mockMvc
            .perform(
                post("/saveIdentifiedPlant")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody)
                    .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Plant saved successfully"));
  }

  @When("I input a {string}, {string}, {string} and {string}")
  public void i_input_a_and(String name, String description, String latitude, String longitude) {
    inputName = name;
    inputDescription = description;
    inputLatitude = latitude;
    inputLongitude = longitude;
  }

  @Then("If the details are invalid, I get the appropriate {string}")
  public void if_the_details_are_invalid_i_get_the_appropriate_message(String message)
      throws Exception {
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("name", inputName);
    requestBody.put("description", inputDescription);
    requestBody.put("plantLatitude", inputLatitude);
    requestBody.put("plantLongitude", inputLongitude);

    ObjectMapper objectMapper = new ObjectMapper();
    String jsonBody = objectMapper.writeValueAsString(requestBody);

    resultActions =
        mockMvc
            .perform(
                post("/saveIdentifiedPlant")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonBody)
                    .with(csrf()))
            .andExpect(status().isBadRequest());

    MockHttpServletResponse response = resultActions.andReturn().getResponse();
    String responseBody = response.getContentAsString();
    Map<String, String> responseMap = objectMapper.readValue(responseBody, Map.class);

    if (responseMap.containsKey("nameError")) {
      assertEquals(message, responseMap.get("nameError"));
    } else if (responseMap.containsKey("descriptionError")) {
      assertEquals(message, responseMap.get("descriptionError"));
    } else if (responseMap.containsKey("locationError")) {
      assertEquals(message, responseMap.get("locationError"));
    }
    else {
      Assertions.fail("Expected error message not found in response");
    }
  }
}