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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

public class ScanningPlantFeature {
    private MockMvc mockScanControllerMvc;
    private Gardener testGardener;
    private Authentication authentication;
    private GardenerFormService gardenerFormService;
    private ImageService imageService;
    private PlantIdentificationService plantIdentificationService;
    private MockMultipartFile genericImage;
    private ResultActions result;

    @Before("@U7001")
    public void setUp() {
        testGardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");

        authentication = Mockito.mock(Authentication.class);
        Authentication auth = new UsernamePasswordAuthenticationToken("testgardener@gmail.com", "Password1!");
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testgardener@gmail.com");

        plantIdentificationService = Mockito.mock(PlantIdentificationService.class);
        imageService = Mockito.mock(ImageService.class);
        gardenerFormService = Mockito.mock(GardenerFormService.class);
        ScanController scanController = new ScanController(plantIdentificationService, gardenerFormService, imageService);

        mockScanControllerMvc = MockMvcBuilders.standaloneSetup(scanController).build();
    }

    @Given("I have an image of a plant")
    public void i_have_an_image_of_a_plant() {
        genericImage = Mockito.mock(MockMultipartFile.class);
    }

    @When("I upload the image of the plant")
    public void i_upload_the_image_of_the_plant() throws Exception {
//        List<String> commonNames = new ArrayList<>(List.of("Common Oak", "English oak", "Carvalho-alvarinho"));
//        IdentifiedPlant identifiedPlant = new IdentifiedPlant("Quercus robor L.", JsonNode result,
//            commonNames , testGardener , "/images/plantImageExample/oakTree.jpg");
        when(genericImage.isEmpty()).thenReturn(false);
        when(imageService.checkValidImage(any())).thenReturn(Optional.empty());

        IdentifiedPlant identifiedPlant = Mockito.mock(IdentifiedPlant.class);
        when(plantIdentificationService.identifyPlant(any(),any())).thenReturn(identifiedPlant);
        when(identifiedPlant.getBestMatch()).thenReturn("Oak");
        when(identifiedPlant.getScore()).thenReturn(0.4);
        when(identifiedPlant.getCommonNames()).thenReturn(List.of("Oaky"));
        when(identifiedPlant.getGbifId()).thenReturn("1234");
        when(identifiedPlant.getImageUrl()).thenReturn("ImageUrl");

        result =  mockScanControllerMvc.perform(MockMvcRequestBuilders.multipart("/identifyPlant")
                .file(genericImage)
                .with(csrf()));
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
//        assertEquals(result, "yes");
    }

    @Then("the app should accurately identify the plant species")
    public void the_app_should_accurately_identify_the_plant_species() throws Exception {
//        result
//                .andExpect(status().isOk());
    }

    @Then("display the name and relevant details")
    public void display_the_name_and_relevant_details() {

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
