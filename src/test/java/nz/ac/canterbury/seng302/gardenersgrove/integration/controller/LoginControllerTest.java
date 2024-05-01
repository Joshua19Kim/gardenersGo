package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nz.ac.canterbury.seng302.gardenersgrove.controller.LoginController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private HttpServletResponse response;
    @MockBean
    private HttpServletRequest request;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private Authentication authentication;


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

}
