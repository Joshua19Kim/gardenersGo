package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import nz.ac.canterbury.seng302.gardenersgrove.GardenersGroveApplication;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GardenFormController.class)
public class GardenLocationAPIFeature {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private GardenerFormService gardenerFormService;

    private Gardener gardener;

    private String city;

    private String country;

    private String address;

    private String gardenName;

    private ResultActions resultActions;

    @BeforeEach
    public void setUp() {
        gardener = new Gardener("Jeff", "Ryan",
                LocalDate.of(2001, 10, 10), "test@gmail.com", "password");
        when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(gardener));
        when(gardenService.getGardensByGardenerId(gardener.getId())).thenReturn(anyList());
        gardenName = "My Garden";
        address = "";
        country = "";
        city = "";
    }

    @Given("I have provided the street number and name {string} and the country {string}")
    public void i_have_provided_the_street_number_and_name_and_the_country(String address, String country) {
        this.address = address;
        this.country = country;
    }

    @When("I submit the create new garden form")
    @WithMockUser
    public void i_submit_the_create_new_garden_form() throws Exception {
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                .param("name", gardenName)
                .param("location", address)
                .param("suburb", "")
                .param("city", city)
                .param("country", country)
                .param("postcode", "")
                .param("size","")
                .param("redirect","")
                .with(csrf()));
    }

    @Then("an error message tells me {string}")
    public void an_error_message_tells_me(String errorMessage) throws Exception {
        if(city.isEmpty()) {
            resultActions.andExpect(model().attribute("countryError", errorMessage));
        } else {
            resultActions.andExpect(model().attribute("cityError", errorMessage));
        }

    }

    @Given("I have provided the street number and name {string} and the city {string}")
    public void i_have_provided_the_street_number_and_name_and_the_city(String address, String city) {
        this.address = address;
        this.city = city;
    }



}
