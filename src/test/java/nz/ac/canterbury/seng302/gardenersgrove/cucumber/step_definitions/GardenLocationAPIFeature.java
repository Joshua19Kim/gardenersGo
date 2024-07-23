package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import nz.ac.canterbury.seng302.gardenersgrove.GardenersGroveApplication;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class GardenLocationAPIFeature {

    private MockMvc mockMvc;

    @Mock
    private Authentication authentication;

    @Mock
    private GardenService gardenService;

    @Mock
    private GardenerFormService gardenerFormService;

    @Mock
    private RelationshipService relationshipService;

    @Mock
    private RequestService requestService;

    @Mock
    private WeatherService weatherService;

    @Mock
    private TagService tagService;

    private Gardener gardener;

    private String city;

    private String country;

    private String address;

    private String gardenName;

    private ResultActions resultActions;

    @Before("@U15_AC5")
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gardener = new Gardener("Jeff", "Ryan",
                LocalDate.of(2001, 10, 10), "test@gmail.com", "password");
        when(gardenerFormService.findByEmail("")).thenReturn(Optional.of(gardener));
        when(gardenService.getGardensByGardenerId(gardener.getId())).thenReturn(List.of());
        when(authentication.getName()).thenReturn("");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        gardenName = "My Garden";
        address = "";
        country = "";
        city = "";
        GardenFormController gardenFormController = new GardenFormController(gardenService, gardenerFormService);
        mockMvc = MockMvcBuilders.standaloneSetup(gardenFormController).build();
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
    }



}
