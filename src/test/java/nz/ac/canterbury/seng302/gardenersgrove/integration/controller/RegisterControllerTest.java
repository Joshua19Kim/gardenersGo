package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;


import nz.ac.canterbury.seng302.gardenersgrove.controller.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegisterController.class)
public class RegisterControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GardenerFormService gardenerFormService;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private TokenService tokenService;
    @MockBean
    private WriteEmail mockWriteEmail;

    @Test
    @WithMockUser
    void onRegisterPage_noInputGiven_RegisterFormShown() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/register")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    @WithMockUser
    void onRegisterPage_validInputGiven_UserCreated() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.doNothing().when(mockWriteEmail).sendSignupEmail(Mockito.any(Gardener.class), Mockito.any(TokenService.class));

        RegisterController registerController = new RegisterController(gardenerFormService, tokenService, mockWriteEmail);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(registerController).build();
        MOCK_MVC
                .perform(MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("firstName", "test")
                        .param("lastName", "test")
                        .param("email", "test@gmail.com")
                        .param("password", "Password1!")
                        .param("passwordConfirm", "Password1!")
                        .param("isLastNameOptional", "false")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/signup"));
        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    @WithMockUser
    void onRegisterPage_invalidFirstName_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("firstName", "123")
                        .param("lastName", "hi")
                        .param("email", "test@gmail.com")
                        .param("password", "Password1!")
                        .param("passwordConfirm", "Password1!")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("firstNameValid"))
                .andExpect(model().attribute("firstNameValid", "First name cannot be empty and must only include letters, spaces, " +
                        "hyphens or apostrophes"));
    }

    @Test
    @WithMockUser
    void lastNameNotOptional_invalidLastName_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("firstName", "test")
                        .param("lastName", "123")
                        .param("email", "test@gmail.com")
                        .param("password", "Password1!")
                        .param("passwordConfirm", "Password1!")
                        .param("isLastNameOptional", "false")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("lastNameValid"))
                .andExpect(model().attribute("lastNameValid", "Last name cannot be empty and must only include letters, spaces, " +
                        "hyphens or apostrophes"));
    }

    @Test
    @WithMockUser
    void lastNameOptional_invalidLastName_userCreated() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        RegisterController registerController = new RegisterController(gardenerFormService, tokenService, mockWriteEmail);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(registerController).build();
        MOCK_MVC
                .perform(MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("firstName", "test")
                        .param("lastName", "123")
                        .param("email", "test@gmail.com")
                        .param("password", "Password1!")
                        .param("passwordConfirm", "Password1!")
                        .param("isLastNameOptional", "true")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/signup"));
        Mockito.verify(gardenerFormService, times(1)).addGardener(Mockito.any(Gardener.class));
    }

    @Test
    @WithMockUser
    void onRegisterPage_ageTooHigh_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("firstName", "test")
                        .param("lastName", "test")
                        .param("email", "test@gmail.com")
                        .param("password", "Password1!")
                        .param("passwordConfirm", "Password1!")
                        .param("DoB", LocalDate.of(1000, 1, 1).toString())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("DoBValid"))
                .andExpect(model().attribute("DoBValid", "The maximum age allowed is 120 years"));
    }

    @Test
    @WithMockUser
    void onRegisterPage_ageTooLow_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("firstName", "test")
                        .param("lastName", "test")
                        .param("email", "test@gmail.com")
                        .param("password", "Password1!")
                        .param("passwordConfirm", "Password1!")
                        .param("DoB", LocalDate.of(3000, 1, 1).toString())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("DoBValid"))
                .andExpect(model().attribute("DoBValid", "You must be 13 years or older to create an account"));
    }

    @Test
    @WithMockUser
    void onRegisterPage_invalidEmail_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("firstName", "test")
                        .param("lastName", "test")
                        .param("email", "test")
                        .param("password", "Password1!")
                        .param("passwordConfirm", "Password1!")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("emailValid"))
                .andExpect(model().attribute("emailValid", "Email address must be in the form 'jane@doe.nz'"));
    }

    @Test
    @WithMockUser
    void onRegisterPage_passwordsTooShort_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("firstName", "test")
                        .param("lastName", "test")
                        .param("email", "test")
                        .param("password", "Pass")
                        .param("passwordConfirm", "Pass")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("passwordStrong"))
                .andExpect(model().attribute("passwordStrong", "Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character."));
    }

    @Test
    @WithMockUser
    void onRegisterPage_passwordsDontMatch_errorMessageProvided() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/register")
                        .with(csrf())
                        .param("firstName", "test")
                        .param("lastName", "test")
                        .param("email", "test")
                        .param("password", "Password1!")
                        .param("passwordConfirm", "Password2@")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("passwordsMatch"))
                .andExpect(model().attribute("passwordsMatch", "Passwords do not match."));
    }

}
