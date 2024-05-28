package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GardenLocationAPIFeature {

    @Given("I am on the create new garden form")
    public void i_am_on_the_create_new_garden_form() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("I provide the street number and name {string} and the country {string}")
    public void i_provide_the_street_number_and_name_and_the_country(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("an error message tells me {string}")
    public void an_error_message_tells_me(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I provide the street number and name {string} and the city {string}")
    public void i_provide_the_street_number_and_name_and_the_city(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
