package nz.ac.canterbury.seng302.gardenersgrove.unit;

import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.InputValidationUtil;
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
import static org.mockito.Mockito.times;

public class UserProfileControllerTest {

    private UserProfileController userProfileController;
    private GardenerFormService gardenerFormService;
    private EmailUserService emailUserService;
    private WriteEmail writeEmail;
    private Model modelMock;
    private Gardener gardener;
    private Authentication authentication;
    private InputValidationUtil inputValidator;
    private Optional optional;


    @BeforeEach
    public void setUp() {
        authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        gardenerFormService = Mockito.mock(GardenerFormService.class);
        emailUserService = Mockito.mock(EmailUserService.class);
        writeEmail = Mockito.mock(WriteEmail.class);
        userProfileController = new UserProfileController(gardenerFormService, writeEmail);
        modelMock = Mockito.mock(Model.class);
        gardener = Mockito.mock(Gardener.class);
        inputValidator = Mockito.mock(InputValidationUtil.class);
        optional = Mockito.mock(Optional.class);
        gardener.setEmail("testEmail@test.test");
    }

    @Test
    void GivenGardenerEmailExistingInServer_WhenToShowDetails_ControllerFindsDetailsWithEmail() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("testEmail@test.test");
        userProfileController.getUserProfile(null, null, null, null, false, null, modelMock);
        Mockito.verify(gardenerFormService, times(1)).findByEmail(gardener.getEmail());
    }

    @Test
    void GivenValidGardenerEdit_WhenUserConfirms_GardenerEditUploaded() {
        Mockito.when(authentication.getName()).thenReturn("new@new.new");
        Mockito.when(gardenerFormService.findByEmail(Mockito.any())).thenReturn(Optional.ofNullable(gardener));
        Mockito.when(optional.get()).thenReturn(gardener);
        Mockito.when(inputValidator.checkValidEmail(Mockito.any())).thenReturn(Optional.empty());
        // ONLY works when the email is the same as the submitted one
        Mockito.when(gardener.getEmail()).thenReturn("new@new.new");
        Mockito.when(authentication.getName()).thenReturn("new@new.new");
        userProfileController.getUserProfile("Ben", "Moore", LocalDate.of(2001, 11, 11), "new@new.new", false, null,modelMock);
        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenInvalidFirstNameEdit_WhenUserConfirms_GardenerEditNotUploaded() {
        userProfileController.getUserProfile("$#@", "Desai", LocalDate.of(2004, 1, 15), "test@gmail.com", false, null, modelMock);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenInvalidLastNameEdit_WhenLastNameIsNotOptional_GardenerEditNotUploaded() {
        userProfileController.getUserProfile("Kush", "$#@", LocalDate.of(2004, 1, 15), "test@gmail.com", false, null, modelMock);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenInvalidLastName_WhenLastNameIsOptional_NewGardenerCreated() {
        Mockito.when(authentication.getName()).thenReturn("test@gmail.com");
        Mockito.when(gardenerFormService.findByEmail(Mockito.any())).thenReturn(Optional.ofNullable(gardener));
        Mockito.when(optional.get()).thenReturn(gardener);
        Mockito.when(inputValidator.checkValidEmail(Mockito.any())).thenReturn(Optional.empty());
        // ONLY works when the email is the same as the submitted one
        Mockito.when(gardener.getEmail()).thenReturn("test@gmail.com");
        Mockito.when(authentication.getName()).thenReturn("test@gmail.com");
        userProfileController.getUserProfile("Kush", "$#@", LocalDate.of(2004, 1, 15), "test@gmail.com", true, null, modelMock);
        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenAgeTooLow_WhenUserConfirms_GardenerEditNotUploaded() {
        userProfileController.getUserProfile("Kush", "Desai", LocalDate.of(2024, 1, 15), "test@gmail.com", false, null, modelMock);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenAgeTooHigh_WhenUserConfirms_GardenerEditNotUploaded() {
        userProfileController.getUserProfile("Kush", "Desai", LocalDate.of(1024, 1, 15), "test@gmail.com", false, null, modelMock);
        Mockito.verify(gardenerFormService, Mockito.never()).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    void GivenIncorrectOldPasswordAndMatchingNewValidPasswords_WhenUserConfirms_DoNotSaveAndShowsErrorMessage() {
        Mockito.when(authentication.getName()).thenReturn("testSameEmail@test.test");
        Mockito.when(gardenerFormService.findByEmail("testSameEmail@test.test")).thenReturn(Optional.of(gardener));
        String passwordInServer = "Password1!";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        Mockito.when(gardener.getPassword()).thenReturn(encoder.encode(passwordInServer));

        String testResult = userProfileController.updatePassword("wrongPassword1!","newPassword1!", "newPassword1!", modelMock);

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

        String testResult = userProfileController.updatePassword("Password1!","newDifferentPassword1@@", "newPassword1!", modelMock);

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

        String testResult = userProfileController.updatePassword("Password1!","newpassword", "newpassword", modelMock);

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

        String testResult = userProfileController.updatePassword("Passwrong1@#","newpass", "newpassword", modelMock);

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
        String testResult = userProfileController.updatePassword("dodgyAccess","dodgyAccess!", "dodgyAccess!", modelMock);
        String expectedNextPage = "login";
        assertEquals(expectedNextPage, testResult);
    }
}
