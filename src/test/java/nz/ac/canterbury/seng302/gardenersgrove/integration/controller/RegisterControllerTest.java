package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;


import nz.ac.canterbury.seng302.gardenersgrove.controller.RegisterController;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

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
    @Test
    @WithMockUser
    void onRegisterPage_noInputGiven_RegisterFormShown() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/register")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }




}
