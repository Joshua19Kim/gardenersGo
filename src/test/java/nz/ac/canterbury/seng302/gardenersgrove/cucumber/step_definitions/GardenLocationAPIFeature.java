package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class GardenLocationAPIFeature {

    @Autowired
    private MockMvc mockMvc;
    private String city;
    private String country;
    private String address;

    private ResultActions resultActions;


    @Given("I have provided the street number and name {string} and the country {string}")
    public void i_have_provided_the_street_number_and_name_and_the_country(String address, String country) {
        this.address = address;
        this.country = country;
        this.city = "";
    }

    @When("I submit the create new garden form")
    @WithMockUser
    public void i_submit_the_create_new_garden_form() throws Exception {
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                .param("name", "test garden name")
                .param("location", address)
                .param("suburb", "")
                .param("city", city)
                .param("country", country)
                .param("postcode", "")
                .param("size","")
                .param("description", "")
                .param("redirect","")
                .with(csrf()));

    }

    @Then("an error message tells me {string}")
    public void an_error_message_tells_me(String errorMessage) throws Exception {
        if(country.isEmpty()) {
            resultActions.andExpect(model().attribute("countryError", errorMessage));
        } else {
            resultActions.andExpect(model().attribute("cityError", errorMessage));
        }


    }

    @Given("I have provided the street number and name {string} and the city {string}")
    public void i_have_provided_the_street_number_and_name_and_the_city(String address, String city) {
        this.address = address;
        this.city = city;
        this.country = "";
    }


}
