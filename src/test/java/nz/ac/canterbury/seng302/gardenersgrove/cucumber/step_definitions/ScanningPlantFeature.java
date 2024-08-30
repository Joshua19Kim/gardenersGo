package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;

import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ScanningPlantFeature {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GardenerFormService gardenerFormService;
    private MockMultipartFile imageFile;
    private String errorMessage;

    @Before("@U7001")
    public void setUp() {
        Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail("a@gmail.com");
    }

    @Given("I have an image of a plant")
    public void i_have_an_image_of_a_plant() {
        byte[] imageContent = new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0};
        imageFile = new MockMultipartFile(
                "image",
                "test_image.jpg",
                "image/jpeg",
                imageContent
        );
    }


    @Then("the app displays the name and relevant details after identifying")
    public void the_app_displays_the_name_and_relevant_details_after_identifying() throws Exception {
        // Mock testIdentifiedPlant(contains mock plant details) has been defined in MockConfigurationSteps
        mockMvc
                .perform(MockMvcRequestBuilders.multipart("/identifyPlant")
                        .file(imageFile)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bestMatch").value("Helianthus annuus"))
                .andExpect(jsonPath("$.score").value(0.88))
                .andExpect(jsonPath("$.gbifId").value("5414641"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/sunflower.jpg"));
    }
    @Given("I uploaded an blurry image")
    public void i_uploaded_an_blurry_image() {
        byte[] imageContent = new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0};
        imageFile = new MockMultipartFile(
                "image",
                "test_image.jpg",
                "image/jpeg",
                imageContent
        );
        errorMessage = "Please ensure the plant is taking up most of the frame and the photo is not blurry.";
    }


    @Given("I uploaded an non_plant image")
    public void i_uploaded_an_non_plant_image() {
        byte[] imageContent = new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0};
        imageFile = new MockMultipartFile(
                "image",
                "test_image.jpg",
                "image/jpeg",
                imageContent
        );
        errorMessage = "There is no matching plant with your image. Please try with a different image of the plant.";
    }



    @Then("I should be informed that the app failed to identify plant")
    public void i_should_be_informed_that_the_app_failed_to_identify_plant() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/identifyPlant")
                        .file(imageFile)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(errorMessage));
    }



    @When("I go to the collections page")
    public void i_go_to_the_collections_page() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("I can see my plant in the collection")
    public void i_can_see_my_plant_in_the_collection() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


    @When("I click the add button")
    public void i_click_the_add_button() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("I input a valid name and description")
    public void i_input_a_valid_name_and_description() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
