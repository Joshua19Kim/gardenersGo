package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.service.EmailUserService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ForgotPasswordFormController;
import nz.ac.canterbury.seng302.gardenersgrove.util.InputValidationUtil;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ForgotPasswordFormController.class)
public class ForgotPasswordControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GardenerFormService gardenerFormService;
    @MockBean
    private TokenService tokenService;
    @MockBean
    private EmailUserService emailService;
    @MockBean
    private WriteEmail writeEmail;
    private InputValidationUtil inputValidator;

    @Test
    @WithMockUser
    void onForgotPasswordPage_noInputGiven_ForgotPasswordFormShown() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/forgotPassword")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("forgotPasswordForm"));
    }
    @Test
    @WithMockUser
    void onForgotPasswordPage_validNonExistingEmail_confirmationMessageProvided() throws Exception {
        inputValidator = new InputValidationUtil(gardenerFormService);
        Mockito.when(inputValidator.checkEmailInUse(Mockito.anyString())).thenReturn(Optional.of(""));
        Mockito.when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        ForgotPasswordFormController forgotPasswordFormController = new ForgotPasswordFormController(gardenerFormService, tokenService, emailService, writeEmail);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(forgotPasswordFormController).build();
        MOCK_MVC
                .perform(MockMvcRequestBuilders.post("/forgotPassword")
                        .with(csrf())
                        .param("email", "test@test.nz")
                        .param("url", "http://localhost:8080/forgotPassword")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("forgotPasswordForm"))
                .andExpect(model().attributeExists("returnMessage"))
                .andExpect(model().attribute("returnMessage","An email was sent to the address if it was recognised"));
    }


    @Test
    @WithMockUser
    void onForgotPasswordPage_invalidEmail_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/forgotPassword")
                        .with(csrf())
                        .param("email", "test")
                        .param("url", "http://localhost:8080/forgotPassword")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("forgotPasswordForm"))
                .andExpect(model().attributeExists("emailError"))
                .andExpect(model().attribute("emailError","Email address must be in the form 'jane@doe.nz'"));
    }
}
