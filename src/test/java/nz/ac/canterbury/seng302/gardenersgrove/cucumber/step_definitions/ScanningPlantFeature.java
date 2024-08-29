package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;

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
    private ResultActions resultActions;
    @Autowired
    private GardenerFormService gardenerFormService;
    private MockMultipartFile imageFile;

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



    @Given("I uploaded an invalid image")
    public void i_uploaded_an_invalid_image() {
        // Invalid file
        imageFile = new MockMultipartFile(
                "image",
                "image.text",
                "plain/text",
                "Hello World!".getBytes()
        );
    }

    @When("the app cannot identify the image")
    public void the_app_cannot_identify_the_image() throws Exception {
        // Invalid file
        resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/identifyPlant")
                        .file(imageFile)
                        .with(csrf()));
    }

    @Then("I should be informed that the app failed to identify plant")
    public void i_should_be_informed_that_the_app_failed_to_identify_plant() throws Exception {
        String errorMessage = "Image must be of type png, jpg or svg";

        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(errorMessage));
    }

}
