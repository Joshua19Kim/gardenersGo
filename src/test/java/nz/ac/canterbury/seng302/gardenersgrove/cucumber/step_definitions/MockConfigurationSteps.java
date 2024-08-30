package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;


import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantIdentificationService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

// The base of this file was shown in the code that Matthew suggested to follow in the workshop
// https://eng-git.canterbury.ac.nz/seng302-2024/cucumber-mocking-example
// This class will contain mocking API and steps here can be reused from multiple stories
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class MockConfigurationSteps {
    @Autowired
    private EmailUserService emailUserService;
    @Autowired
    private PlantIdentificationService plantIdentificationService;
    @Autowired
    private GardenerFormService gardenerFormService;
    private Optional<Gardener> gardenerOptional;
    Gardener gardener;
    @Then("send an email.")
    public void send_an_email() {
        Mockito.verify(emailUserService, Mockito.times(1)).sendEmail(anyString(), anyString(), anyString());
        Mockito.doNothing().when(emailUserService).sendEmail(anyString(),anyString(),anyString());
    }

    @Then("I receive an email confirming my account has been blocked for one week")
    public void send_ban_email() {
        Mockito.verify(emailUserService, Mockito.times(1)).sendEmail(anyString(), anyString(), anyString());
        Mockito.doNothing().when(emailUserService).sendEmail(anyString(),anyString(),anyString());
    }

    @When("the app identifies the plant image")
    public void the_app_identifies_the_plant_image() throws IOException {
        gardenerOptional = gardenerFormService.findByEmail("a@gmail.com");
        gardener = gardenerOptional.get();
        IdentifiedPlant testIdentifiedPlant = new IdentifiedPlant(
                "Helianthus annuus",
                0.88,
                List.of("Sunflower", "Rose"),
                "5414641",
                "https://example.com/sunflower.jpg",
                "https://example.com/sunflower.jpg",
                "Helianthus",
                "annuus"
                , gardener
        );
        when(plantIdentificationService.identifyPlant(
                any(MultipartFile.class),
                any(Gardener.class)))
                .thenReturn(testIdentifiedPlant);
    }
    @When("the app identifies the image with very low score")
    public void the_app_identifies_the_image_with_very_low_score() throws IOException {
        gardenerOptional = gardenerFormService.findByEmail("a@gmail.com");
        gardener = gardenerOptional.get();
        IdentifiedPlant testIdentifiedPlant = new IdentifiedPlant(
                "Helianthus annuus",
                0.29,
                List.of("Sunflower", "Rose"),
                "5414641",
                "https://example.com/sunflower.jpg",
                "https://example.com/sunflower.jpg",
                "Helianthus",
                "annuus"
                , gardener
        );
        when(plantIdentificationService.identifyPlant(
                any(MultipartFile.class),
                any(Gardener.class)))
                .thenReturn(testIdentifiedPlant);
    }

    @When("the app cannot identify the image")
    public void the_app_cannot_identify_the_image() throws Exception {
        byte[] imageContent = new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0};
        MockMultipartFile imageService = new MockMultipartFile(
                "image",
                "test_image.jpg",
                "image/jpeg",
                imageContent
        );
        when(plantIdentificationService.identifyPlant(
                any(MultipartFile.class),
                any(Gardener.class))).thenThrow(new IOException("Species not found"));


    }

}
