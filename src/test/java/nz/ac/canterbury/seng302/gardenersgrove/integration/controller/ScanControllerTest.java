package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.ScanController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.ImageService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantIdentificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;



import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = ScanController.class)
public class ScanControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GardenerFormService gardenerFormService;
    @MockBean
    private PlantIdentificationService plantIdentificationService;
    @MockBean
    private ImageService imageService;
    private MockMultipartFile imageFile;

    @BeforeEach
    void setUp() throws IOException {
        Gardener testGardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        Mockito.reset(gardenerFormService);
        List<Authority> userRoles = new ArrayList<>();
        testGardener.setUserRoles(userRoles);
        testGardener.setId(1L);
        gardenerFormService.addGardener(testGardener);
        when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(testGardener));

        IdentifiedPlant testIdentifiedPlant = new IdentifiedPlant(
                "Helianthus annuus",
                0.88,
                List.of("Sunflower", "Rose"),
                "5414641",
                "https://example.com/sunflower.jpg",
                "https://example.com/sunflower.jpg",
                "Helianthus",
                "annuus"
                , testGardener
        );
        when(plantIdentificationService.identifyPlant(
                Mockito.any(MultipartFile.class),
                Mockito.any(Gardener.class)))
                .thenReturn(testIdentifiedPlant);

    }

    @Test
    @WithMockUser
    void OnScanningModal_userHasNotUploadedImageAndClickedIdentifyButton_ShowErrorMessage() throws Exception {
        String errorMessage = "Please add an image to identify.";
        // No image
        imageFile = new MockMultipartFile("image", "", "application/octet-stream", (new byte[0]));

        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart("/identifyPlant")
                        .file(imageFile)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(errorMessage));
    }
    @Test
    @WithMockUser
    void OnScanningModal_userHasPutValidImageAndClickedIdentifyButton_ShowResponseFromAPI() throws Exception {
        // mock image content
        byte[] imageContent = new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0};

        imageFile = new MockMultipartFile(
                "image",
                "test_image.jpg",
                "image/jpeg",
                imageContent
        );

        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart("/identifyPlant")
                        .file(imageFile)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bestMatch").value("Helianthus annuus"))
                .andExpect(jsonPath("$.score").value(0.88))
                .andExpect(jsonPath("$.gbifId").value("5414641"))
                .andExpect(jsonPath("$.imageUrl").value("https://example.com/sunflower.jpg"));
    }

    @Test
    @WithMockUser
    void OnScanningModal_userHasPutNotPlantImageAndClickedIdentifyButton_ShowErrorMessage() throws Exception {
        // mock image content
        byte[] imageContent = new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0};

        imageFile = new MockMultipartFile(
                "image",
                "test_image.image",
                "image/jpeg",
                imageContent
        );
        when(plantIdentificationService.identifyPlant(
                Mockito.any(MultipartFile.class),
                Mockito.any(Gardener.class)))
                .thenThrow(new RuntimeException("Species not found"));

        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart("/identifyPlant")
                        .file(imageFile)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("There is no matching plant with your image. Please try with a different image of the plant."));

}


    @Test
    @WithMockUser
    void OnScanningModal_userHasPutInvalidImageAndClickedIdentifyButton_ShowErrorMessage() throws Exception {
        String errorMessage = "Image must be of type png, jpg or svg";
        // Invalid file
        imageFile = new MockMultipartFile(
                "image",
                "image.text",
                "plain/text",
                "Hello World!".getBytes()
        );
        when(imageService.checkValidImage(imageFile)).thenReturn(Optional.of(errorMessage));
        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart("/identifyPlant")
                        .file(imageFile)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(errorMessage));
    }









}






