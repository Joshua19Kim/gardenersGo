package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ForgotPasswordFormController;
import nz.ac.canterbury.seng302.gardenersgrove.service.InputValidationService;
import nz.ac.canterbury.seng302.gardenersgrove.service.SecurityService;
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
    private SecurityService securityService;
    private InputValidationService inputValidator;

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
        inputValidator = new InputValidationService(gardenerFormService);
        Mockito.when(inputValidator.checkEmailInUse(Mockito.anyString())).thenReturn(Optional.of(""));
        ForgotPasswordFormController forgotPasswordFormController = new ForgotPasswordFormController(gardenerFormService, securityService);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(forgotPasswordFormController).build();
        MOCK_MVC
                .perform(MockMvcRequestBuilders.post("/forgotPassword")
                        .with(csrf())
                        .param("email", "test@test.nz")
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
                )
                .andExpect(status().isOk())
                .andExpect(view().name("forgotPasswordForm"))
                .andExpect(model().attributeExists("returnMessage"))
                .andExpect(model().attribute("returnMessage","Email address must be in the form ‘jane@doe.nz"));
    }
}
