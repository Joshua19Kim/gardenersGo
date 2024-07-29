package nz.ac.canterbury.seng302.gardenersgrove.unit;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers.GardenDetailsController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.InputValidationUtil;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;

public class GardenDetailsControllerTest {

    private GardenDetailsController gardenDetailsController;
    private GardenService mockGardenService;
    private GardenerFormService mockGardenerFormService;
    private RelationshipService mockRelationshipService;
    private RequestService requestService;
    private WeatherService mockWeatherService;
    private TagService mockTagService;

//    private EmailUserService emailUserService;
//    private WriteEmail writeEmail;
    private Model mockModel;
    private Gardener mockGardener;
    private Authentication authentication;
    private InputValidationUtil inputValidator;
    private Optional optional;
    private HttpServletRequest mockRequest;
    private Garden mockGarden;
    private List<Garden> mockGardens;


    @BeforeEach
    public void setUp() {
        // http mocks
        mockRequest = Mockito.mock(HttpServletRequest.class);
        requestService = Mockito.mock(RequestService.class);
        Mockito.when(mockRequest.getRequestURI()).thenReturn("");

        // Service/ Controller mocks
        mockGardenerFormService = Mockito.mock(GardenerFormService.class);
        mockGardenService = Mockito.mock(GardenService.class);
        mockRelationshipService = Mockito.mock(RelationshipService.class);
        mockWeatherService = Mockito.mock(WeatherService.class);
        mockTagService = Mockito.mock(TagService.class);
        inputValidator = Mockito.mock(InputValidationUtil.class);
        gardenDetailsController = new GardenDetailsController(mockGardenService, mockGardenerFormService, mockRelationshipService, requestService, mockWeatherService, mockTagService);
        mockModel = Mockito.mock(Model.class);

        optional = Mockito.mock(Optional.class);

        mockGarden = Mockito.mock(Garden.class);
        mockGardens = new ArrayList<>();

        // Gardener Security Mock
        mockGardener = Mockito.mock(Gardener.class);
        mockGardener.setEmail("testEmail@test.test");
        authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("testEmail@test.test");
        Mockito.when(mockGardenerFormService.findByEmail(any(String.class))).thenReturn(Optional.of(mockGardener));
    }

//    @Test
//    void GivenUsersGardenExistsWithWeather_WhenDetailsRequested_ControllerFindsDetails() throws IOException, URISyntaxException {
//        Mockito.when(mockGardener.getId()).thenReturn(1L);
//        Mockito.when(mockGardenService.getGardensByGardenerId(any(long.class))).thenReturn(mockGardens);
//        Mockito.when(mockGardenService.getGarden(any(long.class))).thenReturn(Optional.of(mockGarden));
//        gardenDetailsController.gardenDetails("1", null, null, null, mockModel, mockRequest);
//        Mockito.verify(mockGardenService, times(1)).addGarden(any(Garden.class));
//    }

    @Test
    void GivenNoUsersGardenExists_WhenDetailsRequested_ControllerRedirects() throws IOException, URISyntaxException {
        Mockito.when(mockGardenService.getGardensByGardenerId(any(long.class))).thenReturn(mockGardens);
        Mockito.when(mockGardenService.getGarden(any(long.class))).thenReturn(Optional.empty());
        gardenDetailsController.gardenDetails("1", null, null, null, mockModel, mockRequest);
        Mockito.verify(mockGardenService, times(0)).addGarden(any(Garden.class));
    }

    @Test
    void GivenGardenIDIsNull_WhenDetailsRequested_ControllerRedirects() throws IOException, URISyntaxException {
        gardenDetailsController.gardenDetails(null, null, null, null, mockModel, mockRequest);
        Mockito.verify(mockGardenService, times(0)).getGardensByGardenerId(any(long.class));
    }

    @Test
    void GivenUsersGardenExistsWithoutWeather_WhenDetailsRequested_ControllerFindsDetails() throws IOException, URISyntaxException {
        gardenDetailsController.gardenDetails("1", null, null, null, mockModel, mockRequest);
    }

    @Test
    void GivenFriendsGardenExists_WhenDetailsRequested_ControllerFindsDetails() throws IOException, URISyntaxException {
        gardenDetailsController.gardenDetails("1", null, null, null, mockModel, mockRequest);
    }

    @Test
    void GivenNonFriendsGardenExists_WhenDetailsRequested_ControllerRedirects() throws IOException, URISyntaxException {
        gardenDetailsController.gardenDetails("1", null, null, null, mockModel, mockRequest);
    }


//
//    @Test
//    void GivenGardenerEmailExistingInServer_WhenToShowDetails_ControllerFindsDetailsWithEmail() {
//
//        userProfileController.getUserProfile(null, null, null, null, false, null, mockRequest, modelMock);
//        Mockito.verify(gardenerFormService, times(1)).findByEmail(gardener.getEmail());
//    }
//
//    @Test
//    void GivenValidGardenerEdit_WhenUserConfirms_GardenerEditUploaded() {
//        Mockito.when(authentication.getName()).thenReturn("new@new.new");
//        Mockito.when(gardenerFormService.findByEmail(any())).thenReturn(Optional.ofNullable(gardener));
//        Mockito.when(optional.get()).thenReturn(gardener);
//        Mockito.when(inputValidator.checkValidEmail(any())).thenReturn(Optional.empty());
//        // ONLY works when the email is the same as the submitted one
//        Mockito.when(gardener.getEmail()).thenReturn("new@new.new");
//        Mockito.when(authentication.getName()).thenReturn("new@new.new");
//        userProfileController.getUserProfile("Ben", "Moore", LocalDate.of(2001, 11, 11), "new@new.new", false, null, mockRequest, modelMock);
//        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
//    }
//
//    @Test
//    void GivenInvalidFirstNameEdit_WhenUserConfirms_GardenerEditNotUploaded() {
//        userProfileController.getUserProfile("$#@", "Desai", LocalDate.of(2004, 1, 15), "test@gmail.com", false, null, mockRequest,modelMock);
//        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
//    }
//
//    @Test
//    void GivenInvalidLastNameEdit_WhenLastNameIsNotOptional_GardenerEditNotUploaded() {
//        userProfileController.getUserProfile("Kush", "$#@", LocalDate.of(2004, 1, 15), "test@gmail.com", false, null, mockRequest, modelMock);
//        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
//    }
//
//    @Test
//    void GivenInvalidLastName_WhenLastNameIsOptional_NewGardenerCreated() {
//        Mockito.when(authentication.getName()).thenReturn("test@gmail.com");
//        Mockito.when(gardenerFormService.findByEmail(any())).thenReturn(Optional.ofNullable(gardener));
//        Mockito.when(optional.get()).thenReturn(gardener);
//        Mockito.when(inputValidator.checkValidEmail(any())).thenReturn(Optional.empty());
//        // ONLY works when the email is the same as the submitted one
//        Mockito.when(gardener.getEmail()).thenReturn("test@gmail.com");
//        Mockito.when(authentication.getName()).thenReturn("test@gmail.com");
//        userProfileController.getUserProfile("Kush", "$#@", LocalDate.of(2004, 1, 15), "test@gmail.com", true, null, mockRequest, modelMock);
//        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
//    }
//
//    @Test
//    void GivenAgeTooLow_WhenUserConfirms_GardenerEditNotUploaded() {
//        userProfileController.getUserProfile("Kush", "Desai", LocalDate.of(2024, 1, 15), "test@gmail.com", false, null, mockRequest, modelMock);
//        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
//    }
//
//    @Test
//    void GivenAgeTooHigh_WhenUserConfirms_GardenerEditNotUploaded() {
//        userProfileController.getUserProfile("Kush", "Desai", LocalDate.of(1024, 1, 15), "test@gmail.com", false, null, mockRequest, modelMock);
//        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
//    }
//
//    @Test
//    void GivenIncorrectOldPasswordAndMatchingNewValidPasswords_WhenUserConfirms_DoNotSaveAndShowsErrorMessage() {
//        Mockito.when(authentication.getName()).thenReturn("testSameEmail@test.test");
//        Mockito.when(gardenerFormService.findByEmail("testSameEmail@test.test")).thenReturn(Optional.of(gardener));
//        String passwordInServer = "Password1!";
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        Mockito.when(gardener.getPassword()).thenReturn(encoder.encode(passwordInServer));
//
//        String testResult = userProfileController.updatePassword("wrongPassword1!","newPassword1!", "newPassword1!", mockRequest, modelMock);
//
//        Mockito.verify(modelMock).addAttribute("passwordCorrect","Your old password is incorrect.");
//        Mockito.verify(modelMock).addAttribute("passwordsMatch","");
//        Mockito.verify(modelMock).addAttribute("passwordStrong","");
//        Mockito.verify(gardener, times(0)).updatePassword("newPassword1!");
//        Mockito.verify(gardenerFormService,times(0)).addGardener(gardener);
//        String expectedNextPage = "password";
//        assertEquals(expectedNextPage, testResult);
//    }
//
//    @Test
//    void GivenCorrectOldPasswordAndNotMatchingNewValidPasswords_WhenUserConfirms_DoNotSaveAndShowsErrorMessage() {
//        Mockito.when(authentication.getName()).thenReturn("testSameEmail@test.test");
//        Mockito.when(gardenerFormService.findByEmail("testSameEmail@test.test")).thenReturn(Optional.of(gardener));
//        String passwordInServer = "Password1!";
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        Mockito.when(gardener.getPassword()).thenReturn(encoder.encode(passwordInServer));
//
//        String testResult = userProfileController.updatePassword("Password1!","newDifferentPassword1@@", "newPassword1!", mockRequest, modelMock);
//
//        Mockito.verify(modelMock).addAttribute("passwordCorrect","");
//        Mockito.verify(modelMock).addAttribute("passwordsMatch","Passwords do not match.");
//        Mockito.verify(modelMock).addAttribute("passwordStrong","");
//        Mockito.verify(gardener, times(0)).updatePassword("newPassword1!");
//        Mockito.verify(gardenerFormService,times(0)).addGardener(gardener);
//        String expectedNextPage = "password";
//        assertEquals(expectedNextPage, testResult);
//    }
//
//    @Test
//    void GivenCorrectOldPasswordAndMatchingNewInvalidPasswords_WhenUserConfirms_DoNotSaveAndShowsErrorMessage() {
//        Mockito.when(authentication.getName()).thenReturn("testSameEmail@test.test");
//        Mockito.when(gardenerFormService.findByEmail("testSameEmail@test.test")).thenReturn(Optional.of(gardener));
//        String passwordInServer = "Password1!";
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        Mockito.when(gardener.getPassword()).thenReturn(encoder.encode(passwordInServer));
//
//        String testResult = userProfileController.updatePassword("Password1!","newpassword", "newpassword", mockRequest, modelMock);
//
//        Mockito.verify(modelMock).addAttribute("passwordCorrect","");
//        Mockito.verify(modelMock).addAttribute("passwordsMatch","");
//        Mockito.verify(modelMock).addAttribute("passwordStrong","Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.");
//        Mockito.verify(gardener, times(0)).updatePassword("newPassword1!");
//        Mockito.verify(gardenerFormService,times(0)).addGardener(gardener);
//        String expectedNextPage = "password";
//        assertEquals(expectedNextPage, testResult);
//    }
//
//    @Test
//    void GivenIncorrectOldPasswordAndNotMatchingNewInvalidPasswords_WhenUserConfirms_DoNotSaveAndShowsErrorMessage() {
//        Mockito.when(authentication.getName()).thenReturn("testSameEmail@test.test");
//        Mockito.when(gardenerFormService.findByEmail("testSameEmail@test.test")).thenReturn(Optional.of(gardener));
//        String passwordInServer = "Password1!";
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        Mockito.when(gardener.getPassword()).thenReturn(encoder.encode(passwordInServer));
//
//        String testResult = userProfileController.updatePassword("Passwrong1@#","newpass", "newpassword", mockRequest, modelMock);
//
//        Mockito.verify(modelMock).addAttribute("passwordCorrect","Your old password is incorrect.");
//        Mockito.verify(modelMock).addAttribute("passwordsMatch","Passwords do not match.");
//        Mockito.verify(modelMock).addAttribute("passwordStrong","Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.");
//        Mockito.verify(gardener, times(0)).updatePassword("newPassword1!");
//        Mockito.verify(gardenerFormService,times(0)).addGardener(gardener);
//        String expectedNextPage = "password";
//        assertEquals(expectedNextPage, testResult);
//    }
//
//    @Test
//    void GivenUserDoesNotHaveEmailInServer_WhenUserAccessesUpdatePasswordPage_RedirectToLoginPage() {
//        Mockito.when(authentication.getName()).thenReturn("");
//        String testResult = userProfileController.updatePassword("dodgyAccess","dodgyAccess!", "dodgyAccess!", mockRequest, modelMock);
//        String expectedNextPage = "loginForm";
//        assertEquals(expectedNextPage, testResult);
//    }
}
