package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ResetPasswordFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.InputValidationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ResetPasswordFormController.class)
public class ResetPasswordControllerTest {

    @MockBean
    private GardenerFormService gardenerFormService;
    @MockBean
    private TokenService tokenService;
    @MockBean
    private InputValidationService inputValidationService;
    private Gardener mockGardener = new Gardener("test", "test", null, "test@test.test","Password1!", "");
    @MockBean
    private WriteEmail mockWriteEmail;


    @Test
    @WithMockUser
    void onResetPasswordPage_invalidTokenGiven_redirectToLogin() throws Exception {
        Mockito.when(tokenService.validateLostPasswordToken(Mockito.anyString())).thenReturn("invalid");
        ResetPasswordFormController resetPasswordFormController = new ResetPasswordFormController(gardenerFormService, tokenService, mockWriteEmail);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(resetPasswordFormController).build();
        MOCK_MVC
                .perform(MockMvcRequestBuilders.get("/resetPassword")
                        .param("token", "invalid")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }
    @Test
    @WithMockUser
    void onResetPasswordPage_expiredTokenGiven_redirectToLoginExpired() throws Exception {
        Mockito.when(tokenService.validateLostPasswordToken(Mockito.anyString())).thenReturn("expired");
        ResetPasswordFormController resetPasswordFormController = new ResetPasswordFormController(gardenerFormService, tokenService,mockWriteEmail);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(resetPasswordFormController).build();
        MOCK_MVC
                .perform(MockMvcRequestBuilders.get("/resetPassword")
                        .param("token", "expired")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login?expired"));
    }
    @Test
    @WithMockUser
    void onResetPasswordPage_validTokenGiven_redirectToResetPage() throws Exception {
        Mockito.when(tokenService.validateLostPasswordToken(Mockito.anyString())).thenReturn(null);
        Mockito.when(tokenService.findGardenerbyToken(Mockito.anyString())).thenReturn(Optional.of(mockGardener));
        ResetPasswordFormController resetPasswordFormController = new ResetPasswordFormController(gardenerFormService, tokenService,mockWriteEmail);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(resetPasswordFormController).build();
        MOCK_MVC
                .perform(MockMvcRequestBuilders.get("/resetPassword")
                        .param("token", "valid")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("resetPasswordForm"));
    }
    @Test
    @WithMockUser
    void onResetPasswordPage_validTokenGivenGardenerNotPresent_redirectToResetPage() throws Exception {
        Mockito.when(tokenService.validateLostPasswordToken(Mockito.anyString())).thenReturn(null);
        Mockito.when(tokenService.findGardenerbyToken(Mockito.anyString())).thenReturn(Optional.empty());
        ResetPasswordFormController resetPasswordFormController = new ResetPasswordFormController(gardenerFormService, tokenService,mockWriteEmail);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(resetPasswordFormController).build();
        MOCK_MVC
                .perform(MockMvcRequestBuilders.get("/resetPassword")
                        .param("token", "valid")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }

    @Test
    @WithMockUser
    void onResetPasswordPage_validPasswordsGiven_redirectToLogin() throws Exception {
        Mockito.when(tokenService.validateLostPasswordToken(Mockito.anyString())).thenReturn(null);
        Mockito.when(tokenService.findGardenerbyToken(Mockito.anyString())).thenReturn(Optional.of(mockGardener));
        ResetPasswordFormController resetPasswordFormController = new ResetPasswordFormController(gardenerFormService, tokenService,mockWriteEmail);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(resetPasswordFormController).build();
        MOCK_MVC
                .perform(MockMvcRequestBuilders.get("/resetPassword")
                        .param("token", "valid")
                );
        //-------------------------------------------------------------------
        inputValidationService = Mockito.mock(InputValidationService.class);
        Mockito.when(inputValidationService.checkPasswordsMatch(Mockito.anyString(),Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(inputValidationService.checkStrongPassword(Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(gardenerFormService.addGardener(mockGardener)).thenReturn(mockGardener);
        Mockito.doNothing().when(mockWriteEmail).sendPasswordUpdateConfirmEmail(mockGardener);
        MOCK_MVC
                .perform(MockMvcRequestBuilders.post("/resetPassword")
                        .param("password", "Password1!")
                        .param("retypePassword", "Password1!")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));
    }

    @Test
    @WithMockUser
    void onResetPasswordPage_weakPasswordsGiven_stayOnResetPasswordPage() throws Exception {
        inputValidationService = Mockito.mock(InputValidationService.class);
        Mockito.when(inputValidationService.checkPasswordsMatch(Mockito.anyString(),Mockito.anyString())).thenReturn(Optional.empty());
        Mockito.when(inputValidationService.checkStrongPassword(Mockito.anyString())).thenReturn(Optional.of("Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));
        ResetPasswordFormController resetPasswordFormController = new ResetPasswordFormController(gardenerFormService, tokenService, mockWriteEmail);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(resetPasswordFormController).build();
        MOCK_MVC
                .perform(MockMvcRequestBuilders.post("/resetPassword")
                        .param("password", "Weak")
                        .param("retypePassword", "Weak")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("resetPasswordForm"));
    }
    @Test
    @WithMockUser
    void onResetPasswordPage_differentPasswordsGiven_stayOnResetPasswordPage() throws Exception {
        inputValidationService = Mockito.mock(InputValidationService.class);
        Mockito.when(inputValidationService.checkPasswordsMatch(Mockito.anyString(),Mockito.anyString())).thenReturn(Optional.of("Passwords do not match."));
        Mockito.when(inputValidationService.checkStrongPassword(Mockito.anyString())).thenReturn(Optional.empty());
        ResetPasswordFormController resetPasswordFormController = new ResetPasswordFormController(gardenerFormService, tokenService, mockWriteEmail);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(resetPasswordFormController).build();
        MOCK_MVC
                .perform(MockMvcRequestBuilders.post("/resetPassword")
                        .param("password", "Password1!")
                        .param("retypePassword", "Suffering1!")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("resetPasswordForm"));
    }
}
