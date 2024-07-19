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
    }

    @Then("It includes a form for registering a new user")
    public void it_includes_a_form_for_registering_a_new_user() {
        assertEquals(Objects.requireNonNull(mvcResult.getModelAndView()).getViewName(), "loginForm");
    }

    @Given("I am on the registration form")
    public void i_am_on_the_registration_form() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I enter valid values for all fields")
    public void i_enter_valid_values_for_all_fields() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I submit the register form")
    public void i_submit_the_register_form() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I am automatically logged in to my new account")
    public void i_am_automatically_logged_in_to_my_new_account() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("I see my user profile page")
    public void i_see_my_user_profile_page() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
