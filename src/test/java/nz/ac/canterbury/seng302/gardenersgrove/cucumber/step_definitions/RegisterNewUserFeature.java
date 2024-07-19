package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.LoginController;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RegisterNewUserFeature {

    private MockMvc mockMvcLogin;
    private MvcResult mvcResult;

    @Before("@U1")
    public void setUp() {
        LoginController loginController = new LoginController();
        mockMvcLogin = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Given("I connect to the system’s main URL")
    public void i_connect_to_the_system_s_main_url() throws Exception {
        mvcResult = mockMvcLogin
                .perform(MockMvcRequestBuilders.get("/login"))
                .andReturn();
    }

    @When("I see the home page")
    public void i_see_the_home_page() {
        assertEquals(mvcResult.getResponse().getStatus(), 200);
        assertEquals(Objects.requireNonNull(mvcResult.getModelAndView()).getViewName(), "loginForm");
    }

    @Then("It includes a button labelled {string}")
    public void it_includes_a_button_labelled(String buttonName) throws UnsupportedEncodingException {
        assertTrue(mvcResult.getResponse().getContentAsString().contains(buttonName));
    }


}
