package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ScanController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(controllers = ScanController.class)
public class ScanControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GardenerFormService gardenerFormService;

    @MockBean
    private BadgeService badgeService;

    @MockBean
    private GardenService gardenService;
    @MockBean
    private IdentifiedPlantService identifiedPlantService;
    @MockBean
    private PlantIdentificationService plantIdentificationService;
    @MockBean
    private ImageService imageService;
    private MockMultipartFile imageFile;

    Gardener testGardener;

    @BeforeEach
    void setUp() throws IOException {
        testGardener = new Gardener("Test", "Gardener",
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
        String errorMessage = "Image must be of type png or jpg";
        // Invalid file
        imageFile = new MockMultipartFile(
                "image",
                "image.text",
                "plain/text",
                "Hello World!".getBytes()
        );
        when(imageService.checkValidPlantImage(imageFile)).thenReturn(Optional.of(errorMessage));
        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart("/identifyPlant")
                        .file(imageFile)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(errorMessage));
    }

  @Test
  @WithMockUser
  void AfterValidScan_UserEntersValidInputsAndClicksAddButton_ShowResponseFromAPI()
      throws Exception {

    // mock image content
    byte[] imageContent = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

    imageFile = new MockMultipartFile("image", "test_image.jpg", "image/jpeg", imageContent);
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("name", "Tomato");
    requestBody.put("description", "Vegetable");

    ObjectMapper objectMapper = new ObjectMapper();
    String jsonBody = objectMapper.writeValueAsString(requestBody);

    Gardener gardener = new Gardener("Test", "Gardener",
          LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
          "Password1!");
    String name = "My Plant";
    String species = "Plant Species";
    LocalDate date = LocalDate.of(2004, 5, 20);
    String description = "Cool plant";
    IdentifiedPlant identifiedPlant = new IdentifiedPlant(name, description, species, date, gardener);

    when(identifiedPlantService.getCollectionPlantCount(testGardener.getId())).thenReturn(1);
    when(identifiedPlantService.getCollectionPlantCount(testGardener.getId())).thenReturn(1);

    this.mockMvc
        .perform(MockMvcRequestBuilders.multipart("/identifyPlant").file(imageFile).with(csrf()))
        .andExpect(status().isOk());

    when(identifiedPlantService.saveIdentifiedPlantDetails(any(IdentifiedPlant.class))).thenReturn(identifiedPlant);


    this.mockMvc
        .perform(
            MockMvcRequestBuilders.multipart("/saveIdentifiedPlant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Plant saved successfully"));

      verify(badgeService, times(1)).checkPlantBadgeToBeAdded(testGardener, 1);
      verify(identifiedPlantService, times(1)).getCollectionPlantCount(testGardener.getId());
      verify(badgeService, times(0)).checkSpeciesBadgeToBeAdded(eq(testGardener), anyInt());
      verify(identifiedPlantService, times(2)).getSpeciesCount(testGardener.getId());

  }

    @Test
    @WithMockUser
    void AfterValidScan_UserEntersValidInputsAndClicksAddButton_ShowSuccessMessage()
            throws Exception {

        // mock image content
        byte[] imageContent = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        imageFile = new MockMultipartFile("image", "test_image.jpg", "image/jpeg", imageContent);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Tomato");
        requestBody.put("description", "Vegetable");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        Gardener gardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        String name = "My Plant";
        String species = "Plant Species";
        LocalDate date = LocalDate.of(2004, 5, 20);
        String description = "Cool plant";
        IdentifiedPlant identifiedPlant = new IdentifiedPlant(name, description, species, date, gardener);

        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart("/identifyPlant").file(imageFile).with(csrf()))
                .andExpect(status().isOk());

        when(identifiedPlantService.saveIdentifiedPlantDetails(any(IdentifiedPlant.class))).thenReturn(identifiedPlant);


        this.mockMvc
                .perform(
                        MockMvcRequestBuilders.multipart("/saveIdentifiedPlant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.savedPlant").value(identifiedPlant.getId()))
                .andExpect(jsonPath("$.message").value("Plant saved successfully"));
    }

    @Test
    @WithMockUser
    void AfterValidScan_UserEntersInvalidNameAndDescriptionAndClicksAddButton_ShowResponseFromAPI()
            throws Exception {
        String name = "J@CK";
        String description = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus el";

        // mock image content
        byte[] imageContent = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};

        imageFile = new MockMultipartFile("image", "test_image.jpg", "image/jpeg", imageContent);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("description", description);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(requestBody);


        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart("/identifyPlant").file(imageFile).with(csrf()))
                .andExpect(status().isOk());

        this.mockMvc
                .perform(
                        MockMvcRequestBuilders.multipart("/saveIdentifiedPlant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .with(csrf()))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.nameError").value("Plant name cannot be empty and must only include letters, spaces, hyphens or apostrophes <br/>"));

        verify(badgeService, never()).checkPlantBadgeToBeAdded(eq(testGardener), anyInt());
        verify(identifiedPlantService, never()).getCollectionPlantCount(testGardener.getId());
        verify(badgeService, never()).checkSpeciesBadgeToBeAdded(eq(testGardener), anyInt());
        verify(identifiedPlantService, never()).getSpeciesCount(testGardener.getId());
    }

    @Test
    @WithMockUser
    void UserUsesCurrentLocationForPlant_SystemFindsCoordinates_ReturnCoordinatesToController() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Plant test name");
        requestBody.put("plantLatitude", "-40");
        requestBody.put("plantLongitude", "70");

        byte[] imageContent = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        imageFile = new MockMultipartFile("image", "test_image.jpg", "image/jpeg", imageContent);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        Gardener gardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        String name = "My Plant";
        String species = "Plant Species";
        LocalDate date = LocalDate.of(2004, 5, 20);
        String description = "Cool plant";
        IdentifiedPlant identifiedPlant = new IdentifiedPlant(name, description, species, date, gardener);

        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart("/identifyPlant").file(imageFile).with(csrf()))
                .andExpect(status().isOk());

        when(identifiedPlantService.saveIdentifiedPlantDetails(any(IdentifiedPlant.class))).thenReturn(identifiedPlant);

        this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/saveIdentifiedPlant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Plant saved successfully"));
    }

    @WithMockUser
    @ParameterizedTest
    @CsvSource(value = {
            "0 : 0",
            "-90 : -180",
            "90 : 180",
            "89 : 179",
            "-90 : 0",
            "0 : -180",
            "0 : 180",
            "90 : 0",
            "'' : ''",
    }, delimiter = ':')
    void UserUsesCurrentLocationForPlant_HasValidCoordinates_ReturnsSuccessMessage(String plantLatitude, String plantLongitude) throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Plant test name");
        requestBody.put("plantLatitude", plantLatitude);
        requestBody.put("plantLongitude", plantLongitude);
        ResultMatcher status =status().isOk();
        String expectedMessage = "Plant saved successfully";

        byte[] imageContent = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        imageFile = new MockMultipartFile("image", "test_image.jpg", "image/jpeg", imageContent);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        Gardener gardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        String name = "My Plant";
        String species = "Plant Species";
        LocalDate date = LocalDate.of(2004, 5, 20);
        String description = "Cool plant";
        IdentifiedPlant identifiedPlant = new IdentifiedPlant(name, description, species, date, gardener);

        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart("/identifyPlant").file(imageFile).with(csrf()))
                .andExpect(status().isOk());

        when(identifiedPlantService.saveIdentifiedPlantDetails(any(IdentifiedPlant.class))).thenReturn(identifiedPlant);
        this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/saveIdentifiedPlant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .with(csrf()))
                .andExpect(status)
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @WithMockUser
    @ParameterizedTest
    @CsvSource(value = {
            "-91 : -181",
            "-91 : 0",
            "0, : -181",
            "0 : 186",
            "91 : 0",
            "'' : 0",
            "0 : ''"
    }, delimiter = ':')
    void UserUsesCurrentLocationForPlant_HasInvalidCoordinates_DoesNotReturn(String plantLatitude, String plantLongitude) throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Plant test name");
        requestBody.put("plantLatitude", plantLatitude);
        requestBody.put("plantLongitude", plantLongitude);
        ResultMatcher status = status().isBadRequest();
        String expectedMessage = "Invalid Field";

        byte[] imageContent = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        imageFile = new MockMultipartFile("image", "test_image.jpg", "image/jpeg", imageContent);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(requestBody);

        Gardener gardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        String name = "My Plant";
        String species = "Plant Species";
        LocalDate date = LocalDate.of(2004, 5, 20);
        String description = "Cool plant";
        IdentifiedPlant identifiedPlant = new IdentifiedPlant(name, description, species, date, gardener);

        this.mockMvc
                .perform(MockMvcRequestBuilders.multipart("/identifyPlant").file(imageFile).with(csrf()))
                .andExpect(status().isOk());

        when(identifiedPlantService.saveIdentifiedPlantDetails(any(IdentifiedPlant.class))).thenReturn(identifiedPlant);
        this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/saveIdentifiedPlant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody)
                                .with(csrf()))
                .andExpect(status)
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }





}






