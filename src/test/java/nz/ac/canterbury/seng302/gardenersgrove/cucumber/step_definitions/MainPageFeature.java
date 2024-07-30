package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.LoginController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MainPageFeature {
    @Autowired
    private MockMvc mockMvcLogin;
    private Authentication authentication;
    private LoginController loginController;
    private Gardener gardener;
    private MvcResult mvcResult;

//    @Before("@U25")
//    public void setUp() {
//        loginController = new LoginController();
//        mockMvcLogin = MockMvcBuilders.standaloneSetup(loginController).build();
//    }

    @Given("I am a valid user")
    public void i_am_a_valid_user() {
        gardener = new Gardener("John", "Doe",
                LocalDate.of(2000, 1, 1), "a@gmail.com",
                "Password1!");
        authentication = new UsernamePasswordAuthenticationToken("a@gmail.com", "Password1!");
//        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
//        securityContext.setAuthentication(authentication);
//        SecurityContextHolder.setContext(securityContext);
    }

    @When("I submit the login form")
    public void i_submit_the_login_form() throws Exception {
        mvcResult = mockMvcLogin.perform(MockMvcRequestBuilders.post("/login"))
                .andReturn();
//        HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
//        HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
//        HttpSession mockSession = Mockito.mock(HttpSession.class);
//        Mockito.when(httpServletRequest.getSession()).thenReturn(mockSession);
//        loginController.login(authentication, httpServletResponse, httpServletRequest);
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/home"));
    }

    @Then("I am taken by default to the home page")
    public void i_am_taken_by_default_to_the_home_page() {
        assertEquals("redirect:/home", mvcResult.getResponse().getForwardedUrl());
    }
}
