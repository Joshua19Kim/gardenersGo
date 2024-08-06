package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;


import io.cucumber.java.en.Given;
import nz.ac.canterbury.seng302.gardenersgrove.authentication.CustomAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;



@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class AuthenticationSteps {

    @Autowired
    private CustomAuthenticationProvider customAuthProvider;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;

    @Given("I am logged in with email {string} and password {string}")
    public void iAmLoggedInWithEmailTestGmailComAndPasswordHunter(String email, String password) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        var auth = new UsernamePasswordAuthenticationToken(email, password, Collections.emptyList());
        var authentication = customAuthProvider.authenticate(auth);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
