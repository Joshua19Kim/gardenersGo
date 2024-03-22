package nz.ac.canterbury.seng302.gardenersgrove.integration;


import nz.ac.canterbury.seng302.gardenersgrove.controller.LoginController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoginController.class)
public class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser()
    void homeURL_redirectsToLogin() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/").with(csrf()))
                .andExpect(redirectedUrl("/login"))
                .andExpect(status().isFound());
    }

}
