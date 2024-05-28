package nz.ac.canterbury.seng302.gardenersgrove.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import nz.ac.canterbury.seng302.gardenersgrove.controller.LoginController;
import org.springframework.web.servlet.ModelAndView;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ResetPasswordFeature {

    private MockMvc mockMvc;
    private MvcResult mvcResult;

    @Before
    public void setUp() {
        LoginController loginController = new LoginController();
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @Given("I am on the login page")
    public void i_am_on_the_login_page() throws Exception {
        mvcResult = mockMvc
                .perform(MockMvcRequestBuilders.get("/login"))
                .andExpect(status().isOk())
        .andReturn();
    }

    @When("I hit the {string} link")
    public void i_hit_the_link(String linkName) {
        ModelAndView modelAndView = mvcResult.getModelAndView();

    }

    @Then("I am taken to the {string} page")
    public void i_am_taken_to_the_page(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
