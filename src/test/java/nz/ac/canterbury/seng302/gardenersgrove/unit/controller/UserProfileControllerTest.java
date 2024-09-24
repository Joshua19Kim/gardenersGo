package nz.ac.canterbury.seng302.gardenersgrove.unit.controller;

import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.MainPageLayout;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.InputValidationUtil;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserProfileControllerTest {

    private UserProfileController userProfileController;
    private GardenService gardenService;
    private GardenerFormService gardenerFormService;
    private EmailUserService emailUserService;
    private WriteEmail writeEmail;
    private Model modelMock;
    private Gardener gardener;
    private Authentication authentication;
    private InputValidationUtil inputValidator;
    private Optional optional;
    private HttpServletRequest mockRequest;
    private RequestService requestService;
    private MainPageLayoutService mainPageLayoutService;
    private BadgeService badgeService;
    private MainPageLayout mainPageLayout;


    @BeforeEach
    public void setUp() {
        authentication = Mockito.mock(Authentication.class);
        mainPageLayout = Mockito.mock(MainPageLayout.class);
        mockRequest = Mockito.mock(HttpServletRequest.class);
        requestService = Mockito.mock(RequestService.class);
        Mockito.when(mockRequest.getRequestURI()).thenReturn("");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        gardenerFormService = Mockito.mock(GardenerFormService.class);
        emailUserService = Mockito.mock(EmailUserService.class);
        writeEmail = Mockito.mock(WriteEmail.class);
        gardenService = Mockito.mock(GardenService.class);
        mainPageLayoutService = Mockito.mock(MainPageLayoutService.class);
        badgeService = Mockito.mock(BadgeService.class);
        userProfileController = new UserProfileController(gardenerFormService, writeEmail, requestService,
                mainPageLayoutService, badgeService);
        modelMock = Mockito.mock(Model.class);
        gardener = Mockito.mock(Gardener.class);
        inputValidator = Mockito.mock(InputValidationUtil.class);
        optional = Mockito.mock(Optional.class);
        gardener.setEmail("testEmail@test.test");
        when(userProfileController.getGardenerFromAuthentication()).thenReturn(Optional.of(gardener));
        when(mainPageLayout.getWidgetsEnabled()).thenReturn("1 1 1 1");
        when(mainPageLayoutService.getLayoutByGardenerId(any())).thenReturn(mainPageLayout);
    }

    @Test
    void GivenGardenerEmailExistingInServer_WhenToShowDetails_ControllerFindsDetailsWithEmail() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("testEmail@test.test");
        userProfileController.getUserProfile(null, null, null, null, false,false, null, mockRequest, modelMock);
        Mockito.verify(gardenerFormService, times(1)).findByEmail(gardener.getEmail());
    }

    @Test
    void GivenValidGardenerEdit_WhenUserConfirms_GardenerEditUploaded() {
        Mockito.when(authentication.getName()).thenReturn("new@new.new");
        Mockito.when(gardenerFormService.findByEmail(any())).thenReturn(Optional.ofNullable(gardener));
        Mockito.when(optional.get()).thenReturn(gardener);
        Mockito.when(inputValidator.checkValidEmail(any())).thenReturn(Optional.empty());
        // ONLY works when the email is the same as the submitted one
        Mockito.when(gardener.getEmail()).thenReturn("new@new.new");
        Mockito.when(authentication.getName()).thenReturn("new@new.new");
        userProfileController.getUserProfile("Ben", "Moore", LocalDate.of(2001, 11, 11).toString(), "new@new.new", false, false,null, mockRequest, modelMock);
        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenInvalidFirstNameEdit_WhenUserConfirms_GardenerEditNotUploaded() {
        userProfileController.getUserProfile("$#@", "Desai", LocalDate.of(2004, 1, 15).toString(), "test@gmail.com", false,false, null, mockRequest,modelMock);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenInvalidLastName_WhenLastNameIsOptional_NewGardenerCreated() {
        Mockito.when(authentication.getName()).thenReturn("test@gmail.com");
        Mockito.when(gardenerFormService.findByEmail(any())).thenReturn(Optional.ofNullable(gardener));
        Mockito.when(optional.get()).thenReturn(gardener);
        Mockito.when(inputValidator.checkValidEmail(any())).thenReturn(Optional.empty());
        // ONLY works when the email is the same as the submitted one
        Mockito.when(gardener.getEmail()).thenReturn("test@gmail.com");
        Mockito.when(authentication.getName()).thenReturn("test@gmail.com");
        userProfileController.getUserProfile("Kush", "$#@", LocalDate.of(2004, 1, 15).toString(), "test@gmail.com", true,false, null, mockRequest, modelMock);
        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenAgeTooLow_WhenUserConfirms_GardenerEditNotUploaded() {
        userProfileController.getUserProfile("Kush", "Desai", LocalDate.of(2024, 1, 15).toString(), "test@gmail.com", false,false, null, mockRequest, modelMock);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenAgeTooHigh_WhenUserConfirms_GardenerEditNotUploaded() {
        userProfileController.getUserProfile("Kush", "Desai", LocalDate.of(1024, 1, 15).toString(), "test@gmail.com", false,false, null, mockRequest, modelMock);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenDateOverUpperBound_WhenUserConfirms_GardenerEditNotUploaded() {
        userProfileController.getUserProfile("Kush", "Desai", LocalDate.of(9999, 12, 31).toString(), "test@gmail.com", false,false, null, mockRequest, modelMock);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenDateOutOfBounds_WhenUserConfirms_GardenerEditNotUploaded() {
        userProfileController.getUserProfile("Kush", "Desai", LocalDate.of(999999, 12, 31).toString(), "test@gmail.com", false,false, null, mockRequest, modelMock);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenIncorrectOldPasswordAndMatchingNewValidPasswords_WhenUserConfirms_DoNotSaveAndShowsErrorMessage() {
        Mockito.when(authentication.getName()).thenReturn("testSameEmail@test.test");
        Mockito.when(gardenerFormService.findByEmail("testSameEmail@test.test")).thenReturn(Optional.of(gardener));
        String passwordInServer = "Password1!";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Mockito.when(gardener.getPassword()).thenReturn(encoder.encode(passwordInServer));

        String testResult = userProfileController.updatePassword("wrongPassword1!","newPassword1!", "newPassword1!", mockRequest, modelMock);

        Mockito.verify(modelMock).addAttribute("passwordCorrect","Your old password is incorrect.");
        Mockito.verify(modelMock).addAttribute("passwordsMatch","");
        Mockito.verify(modelMock).addAttribute("passwordStrong","");
        Mockito.verify(gardener, times(0)).updatePassword("newPassword1!");
        Mockito.verify(gardenerFormService,times(0)).addGardener(gardener);
        String expectedNextPage = "password";
        assertEquals(expectedNextPage, testResult);
    }

    @Test
    void GivenCorrectOldPasswordAndNotMatchingNewValidPasswords_WhenUserConfirms_DoNotSaveAndShowsErrorMessage() {
        Mockito.when(authentication.getName()).thenReturn("testSameEmail@test.test");
        Mockito.when(gardenerFormService.findByEmail("testSameEmail@test.test")).thenReturn(Optional.of(gardener));
        String passwordInServer = "Password1!";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Mockito.when(gardener.getPassword()).thenReturn(encoder.encode(passwordInServer));

        String testResult = userProfileController.updatePassword("Password1!","newDifferentPassword1@@", "newPassword1!", mockRequest, modelMock);

        Mockito.verify(modelMock).addAttribute("passwordCorrect","");
        Mockito.verify(modelMock).addAttribute("passwordsMatch","Passwords do not match.");
        Mockito.verify(modelMock).addAttribute("passwordStrong","");
        Mockito.verify(gardener, times(0)).updatePassword("newPassword1!");
        Mockito.verify(gardenerFormService,times(0)).addGardener(gardener);
        String expectedNextPage = "password";
        assertEquals(expectedNextPage, testResult);
    }

    @Test
    void GivenCorrectOldPasswordAndMatchingNewInvalidPasswords_WhenUserConfirms_DoNotSaveAndShowsErrorMessage() {
        Mockito.when(authentication.getName()).thenReturn("testSameEmail@test.test");
        Mockito.when(gardenerFormService.findByEmail("testSameEmail@test.test")).thenReturn(Optional.of(gardener));
        String passwordInServer = "Password1!";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Mockito.when(gardener.getPassword()).thenReturn(encoder.encode(passwordInServer));

        String testResult = userProfileController.updatePassword("Password1!","newpassword", "newpassword", mockRequest, modelMock);

        Mockito.verify(modelMock).addAttribute("passwordCorrect","");
        Mockito.verify(modelMock).addAttribute("passwordsMatch","");
        Mockito.verify(modelMock).addAttribute("passwordStrong","Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.");
        Mockito.verify(gardener, times(0)).updatePassword("newPassword1!");
        Mockito.verify(gardenerFormService,times(0)).addGardener(gardener);
        String expectedNextPage = "password";
        assertEquals(expectedNextPage, testResult);
    }

    @Test
    void GivenIncorrectOldPasswordAndNotMatchingNewInvalidPasswords_WhenUserConfirms_DoNotSaveAndShowsErrorMessage() {
        Mockito.when(authentication.getName()).thenReturn("testSameEmail@test.test");
        Mockito.when(gardenerFormService.findByEmail("testSameEmail@test.test")).thenReturn(Optional.of(gardener));
        String passwordInServer = "Password1!";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Mockito.when(gardener.getPassword()).thenReturn(encoder.encode(passwordInServer));

        String testResult = userProfileController.updatePassword("Passwrong1@#","newpass", "newpassword", mockRequest, modelMock);

        Mockito.verify(modelMock).addAttribute("passwordCorrect","Your old password is incorrect.");
        Mockito.verify(modelMock).addAttribute("passwordsMatch","Passwords do not match.");
        Mockito.verify(modelMock).addAttribute("passwordStrong","Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character.");
        Mockito.verify(gardener, times(0)).updatePassword("newPassword1!");
        Mockito.verify(gardenerFormService,times(0)).addGardener(gardener);
        String expectedNextPage = "password";
        assertEquals(expectedNextPage, testResult);
    }

    @Test
    void GivenUserDoesNotHaveEmailInServer_WhenUserAccessesUpdatePasswordPage_RedirectToLoginPage() {
        Mockito.when(authentication.getName()).thenReturn("");
        String testResult = userProfileController.updatePassword("dodgyAccess","dodgyAccess!", "dodgyAccess!", mockRequest, modelMock);
        String expectedNextPage = "loginForm";
        assertEquals(expectedNextPage, testResult);
    }
}
