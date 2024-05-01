package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.ResetPasswordFormController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.SignupCodeFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Authority;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.LostPasswordToken;
import nz.ac.canterbury.seng302.gardenersgrove.service.AuthorityFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TokenService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(SignupCodeFormController.class)
public class SignupCodeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    GardenerFormService gardenerFormService;
    @MockBean
    AuthorityFormService authorityFormService;
    @MockBean
    TokenService tokenService;
    private Gardener mockGardener = new Gardener("test", "test", null, "test@test.test","Password1!");

    @Test
    @WithMockUser
    void onSignupCodePage_noInputGiven_signupCodeFormShown() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signupCodeForm"));
    }
    @Test
    @WithMockUser
    void onSignupCodePage_invalidToken_redirectToSignup() throws Exception{
        Mockito.when(tokenService.validateLostPasswordToken(Mockito.anyString())).thenReturn("invalidToken");
        SignupCodeFormController signupCodeFormController = new SignupCodeFormController(gardenerFormService,authorityFormService,tokenService);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(signupCodeFormController).build();
        MOCK_MVC.perform(MockMvcRequestBuilders.post("/signup")
                        .param("signupToken", "invalid"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/signup?invalid"));
    }

    @Test
    @WithMockUser
    void onSignupCodePage_validToken_redirectToLogin() throws Exception{
        List<Authority> userRoles = new ArrayList<>();
        mockGardener.setUserRoles(userRoles);
        Mockito.when(tokenService.validateLostPasswordToken(Mockito.anyString())).thenReturn(null);
        Mockito.when(tokenService.findGardenerbyToken(Mockito.anyString())).thenReturn(Optional.of(mockGardener));
        Mockito.when(tokenService.getTokenFromString(Mockito.anyString())).thenReturn(Optional.of(Mockito.mock(LostPasswordToken.class)));
        Mockito.doNothing().when(tokenService).removeToken(Mockito.any(LostPasswordToken.class));
        Mockito.when(gardenerFormService.addGardener(Mockito.mock(Gardener.class))).thenReturn(Mockito.mock(Gardener.class));
        SignupCodeFormController signupCodeFormController = new SignupCodeFormController(gardenerFormService,authorityFormService,tokenService);
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(signupCodeFormController).build();
        MOCK_MVC.perform(MockMvcRequestBuilders.post("/signup")
                        .param("signupToken", "valid"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login?signedup"));
    }
}
