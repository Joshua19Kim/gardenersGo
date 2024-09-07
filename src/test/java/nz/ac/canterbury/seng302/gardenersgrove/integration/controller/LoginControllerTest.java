package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;


import jakarta.servlet.http.HttpServletRequest;
import nz.ac.canterbury.seng302.gardenersgrove.controller.LoginController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private HttpServletRequest request;
    @MockBean
    private GardenerFormService gardenerFormService;
    @MockBean
    private GardenService gardenService;

    @Test
    @WithMockUser
    void homeURL_redirectsToLogin() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/")
                        .with(csrf()))
                .andExpect(redirectedUrl("/login"))
                .andExpect(status().isFound());
    }

    @Test
    void onLoginPage_notLoggedIn_StaysOnLoginPage() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void onLoginPage_notVerified_redirectsToSignUpCodeForm() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", "Email not verified");
        Mockito.when(request.getSession()).thenReturn(session);
        LoginController loginController = new LoginController();
        MockMvc MOCK_MVC = MockMvcBuilders.standaloneSetup(loginController).build();
        MOCK_MVC.perform(MockMvcRequestBuilders.get("/login").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/signup"));
    }

    @Test
    void onLoginPage_userIsBanned_redirectedWithError() throws Exception {
        Gardener gardener = new Gardener("Test", "Gardener", LocalDate.of(2000, 1, 1), "test@test.com", "Password1!");
        gardener.banGardener();
        Mockito.when(gardenerFormService.getUserByEmailAndPassword(gardener.getEmail(), gardener.getPassword())).thenReturn(java.util.Optional.of(gardener));

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .param("username", gardener.getEmail())
                .param("password", gardener.getPassword())
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

}
