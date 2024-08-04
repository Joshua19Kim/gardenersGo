package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MainPageFeature {
    @Autowired
    private MockMvc mockMvcLogin;
    private Gardener gardener;
    private ResultActions resultActions;

    @Given("I am a valid user")
    public void i_am_a_valid_user() {
        gardener = new Gardener("John", "Doe", LocalDate.of(2000, 1, 1), "a@gmail.com", "Password1!");
    }

    @When("I submit the login form")
    public void i_submit_the_login_form() throws Exception {
        resultActions = mockMvcLogin.perform(MockMvcRequestBuilders.post("/login")
                .param("username", gardener.getEmail())
                .param("password", "Password1!").with(csrf()));
    }

    @Then("I am taken by default to the home page")
    public void i_am_taken_by_default_to_the_home_page() throws Exception {
        MvcResult mvcResult = resultActions.andExpect(status().is3xxRedirection()).andReturn();
        assertEquals("/home", mvcResult.getResponse().getHeader("Location"));
    }
}
