package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ScanController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantIdentificationService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.xml.transform.Result;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ScanningPlantFeature {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GardenerFormService gardenerFormService;
    private MockMultipartFile imageFile;
    private Gardener gardener;
    private ResultActions resultActions;

    @Before("@U7001")
    public void setUp() {
        Optional<Gardener> gardenerOptional = gardenerFormService.findByEmail("a@gmail.com");
        gardener = gardenerOptional.get();
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
        mockMvc.perform(MockMvcRequestBuilders.multipart("/identifyPlant")
                        .file(imageFile)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bestMatch").value("Helianthus annuus"))
                .andExpect(jsonPath("$.score").value(0.88))
                .andExpect(jsonPath("$.gbifId").value("5414641"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/sunflower.jpg"));
    }





    @When("I upload the image of the plant which cant be identified")
    public void i_upload_the_image_of_the_plant_which_cant_be_identified() throws Exception {

    }

    @Then("I should be informed that no species was identified")
    public void i_should_be_informed_that_no_species_was_identified() {

    }

    @Given("I am on the plant identification page")
    public void i_am_on_the_plant_identification_page() {

    }

    @Then("I see appropriate attribution text")
    public void i_see_appropriate_attribution_text() {

    }

}
