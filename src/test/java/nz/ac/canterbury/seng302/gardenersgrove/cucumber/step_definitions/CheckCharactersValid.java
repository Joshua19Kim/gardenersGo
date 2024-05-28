package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import nz.ac.canterbury.seng302.gardenersgrove.controller.UserProfileController;
import nz.ac.canterbury.seng302.gardenersgrove.util.InputValidationUtil;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.ParameterType;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CheckCharactersValid {
    private final GardenerFormService gardenerFormService = new GardenerFormService(null, null);
    private final InputValidationUtil inputValidationService = new InputValidationUtil(gardenerFormService);
    private final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    private String nameToTest = "";
    private Optional<String> errorMessage;

    @ParameterType(".*") // This matches any string
    public String message(String message) {
        return message;
    }
    @Given("I enter a {string} in the first name input field less than 65 characters")
    public void i_enter_a_name_in_the_first_name_input_field_less_than_65_characters(String name) {
        nameToTest = name;
    }
    @When("I click the register button of the form")
    public void i_click_the_register_button_of_the_form() {
        errorMessage = inputValidationService.checkValidName(nameToTest, "First", false);
    }
    @Then("If the name uses invalid characters, I get the appropriate {message}")
    public void if_the_name_uses_invalid_characters_i_get_the_appropriate(String message) {
        assertEquals(errorMessage.orElse("No message"), message);
    }

}
