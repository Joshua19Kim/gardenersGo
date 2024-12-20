package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// The base of this file was shown in the code that Matthew suggested to follow in the workshop
// https://eng-git.canterbury.ac.nz/seng302-2024/cucumber-mocking-example
// This class will contain mocking API and steps here can be reused from multiple stories
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class MockConfigurationSteps {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EmailUserService emailUserService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private PlantIdentificationService plantIdentificationService;
    @Autowired
    private GardenerFormService gardenerFormService;
    private Optional<Gardener> gardenerOptional;
    private IdentifiedPlant testIdentifiedPlant;
    Gardener gardener;

    @Then("send an email.")
    public void send_an_email() {
        Mockito.doNothing().when(emailUserService).sendEmail(anyString(),anyString(),anyString());
        Mockito.verify(emailUserService, Mockito.times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Then("I receive an email confirming my account has been blocked for one week")
    public void send_ban_email() {
        Mockito.doNothing().when(emailUserService).sendEmail(anyString(),anyString(),anyString());
        Mockito.verify(emailUserService, Mockito.times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @When("the app identifies the plant image")
    public void the_app_identifies_the_plant_image() throws IOException {
        gardenerOptional = gardenerFormService.findByEmail("a@gmail.com");
        gardener = gardenerOptional.get();
        testIdentifiedPlant = new IdentifiedPlant(
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

    @When("I have collected another region")
    public void i_have_collected_another_region() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("plantImage", "image.jpg", "image/jpeg", new byte[0]);
        String manualPlantLat = "-43.522783";
        String manualPlantLon = "172.581256";
        when(locationService.sendReverseGeocodingRequest(manualPlantLat, manualPlantLon)).thenReturn("Chatham Islands");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/myCollection")
                        .file(imageFile)
                        .param("plantName", "New Plant" )
                        .param("description", "Awesome description")
                        .param("scientificName",  "Sick species")
                        .param("uploadedDate", String.valueOf(LocalDate.parse("12/02/2024", DateTimeFormatter.ofPattern("dd/MM/yyyy"))))
                        .param("isDateInvalid", "false")
                        .param("manualPlantLat", manualPlantLat)
                        .param("manualPlantLon", manualPlantLon)
                        .with(csrf()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/myCollection"));
    }
}
