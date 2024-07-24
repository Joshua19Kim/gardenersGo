package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers.GardenEditController;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Authority;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class PubliciseGardensFeature {

    private MockMvc mockGardenFormControllerMvc;

    private MockMvc mockGardenEditControllerMvc;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private GardenerFormService gardenerFormService;

    @MockBean
    private Authentication authentication;

    private Gardener testGardener;
    private String testGardenDescription;
    private long testGardenId;
    private String testGardenName;
    private String testStreetNumberName;
    private String testSuburb;
    private String testCity;
    private String testCountry;
    private String testPostcode;
    private String testSize;
    private String editTestGardenDescription;
    private Garden testGarden;
    private MvcResult result;

    @Before("@U19")
    public void setUp() {
        testGardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        testGardenId = 1L;
        testGardenName = "Rose Garden";
        testStreetNumberName = "5 test street";
        testSuburb = "Ilam";
        testCity = "Christchurch";
        testCountry = "New Zealand";
        testPostcode = "9999";
        testSize = "100";

        authentication = Mockito.mock(Authentication.class);
        gardenService = Mockito.mock(GardenService.class);
        Authentication auth = new UsernamePasswordAuthenticationToken("testgardener@gmail.com", "Password1!");
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);
        gardenerFormService = Mockito.mock(GardenerFormService.class);
        when(authentication.getName()).thenReturn("testgardener@gmail.com");

        GardenFormController gardenFormController = new GardenFormController(gardenService, gardenerFormService);
        GardenEditController gardenEditController = new GardenEditController(gardenService, gardenerFormService,
                new RequestService());

        mockGardenFormControllerMvc = MockMvcBuilders.standaloneSetup(gardenFormController).build();
        mockGardenEditControllerMvc = MockMvcBuilders.standaloneSetup(gardenEditController).build();
    }

    //AC2,3
    @Given("I am on Create New Garden form")
    public void i_on_create_new_garden_form() {
        List<Authority> userRoles = new ArrayList<>();
        testGardener.setUserRoles(userRoles);
        testGardener.setId(1L);
        when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(testGardener));
    }

    //AC2
    @When("I add valid description of the garden")
    public void i_add_valid_description_of_the_garden(){
        testGardenDescription = "testing description function!";
    }
    //AC3
    @When("I do not add any description")
    public void i_do_not_add_any_description() {
        testGardenDescription = "";
    }
    @When("I submit the create form")
    public void i_submit_the_create_form() throws Exception {
        testGarden = new Garden(testGardenName, testStreetNumberName, testSuburb, testCity, testCountry, testPostcode, testSize, testGardener, testGardenDescription);
        testGarden.setId(testGardenId);
        when(gardenService.addGarden(any(Garden.class))).thenReturn(testGarden);
        mockGardenFormControllerMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", testGardenName)
                        .param("location", testStreetNumberName)
                        .param("suburb", testSuburb)
                        .param("city", testCity)
                        .param("country", testCountry)
                        .param("postcode", testPostcode)
                        .param("size", testSize)
                        .param("description", testGardenDescription)
                        .param("redirect", "")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
    }
    @Then("The new description is persisted.")
    public void the_new_description_is_persisted() {
        ArgumentCaptor<Garden> gardenCaptor = ArgumentCaptor.forClass(Garden.class);
        verify(gardenService, Mockito.times(1)).addGarden(gardenCaptor.capture());
        List<Garden> addGarden = gardenCaptor.getAllValues();
        assertEquals(testGardenDescription, addGarden.getFirst().getDescription());
    }



    @Given("I am on the Edit Garden form for one of the existing garden")
    public void i_am_on_the_edit_garden_form_for_one_of_the_existing_garden() {
        List<Authority> userRoles = new ArrayList<>();
        testGardener.setUserRoles(userRoles);
        testGardener.setId(1L);
        when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(testGardener));
    }

    @When("I add valid description of the garden to update")
    public void i_add_valid_description_of_the_garden_to_update() {
        testGardenDescription = "Original description!";
        editTestGardenDescription = "Updated description!!!!!!!!!!!!!!!!!!";
    }
    @When("I delete the current description and leave it empty to update")
    public void i_delete_the_current_description_and_leave_it_empty_to_update() {
        testGardenDescription = "Original description!";
        editTestGardenDescription = "";
    }
    @When("I submit the edit form")
    public void i_submit_the_edit_form() throws Exception {
        testGarden = new Garden(testGardenName, testStreetNumberName, testSuburb, testCity, testCountry, testPostcode, testSize, testGardener, testGardenDescription);
        testGarden.setId(testGardenId);
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(testGarden));
        when(gardenService.addGarden(any(Garden.class))).thenReturn(testGarden);
        when(gardenService.getGardensByGardenerId(any())).thenReturn(List.of(testGarden));
        mockGardenEditControllerMvc.perform(MockMvcRequestBuilders.post("/gardens/edit")
                        .param("name", testGardenName)
                        .param("location", testStreetNumberName)
                        .param("suburb", testSuburb)
                        .param("city", testCity)
                        .param("country", testCountry)
                        .param("postcode", testPostcode)
                        .param("size", testSize)
                        .param("description", editTestGardenDescription)
                        .param("gardenId", String.valueOf(testGardenId))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
    }
    @Then("The updated description is persisted.")
    public void the_updated_description_is_persisted() {
        ArgumentCaptor<Garden> gardenCaptor = ArgumentCaptor.forClass(Garden.class);
        verify(gardenService, Mockito.times(1)).addGarden(gardenCaptor.capture());
        List<Garden> addGarden = gardenCaptor.getAllValues();
        assertEquals(editTestGardenDescription, addGarden.getFirst().getDescription());
    }


    @When("I submit the create form with invalid {string} of the garden")
    public void i_submit_the_create_form_with_invalid_of_the_garden(String description) throws Exception {
        testGardenDescription = description;
        testGarden = new Garden(testGardenName, testStreetNumberName, testSuburb, testCity, testCountry, testPostcode, testSize, testGardener, testGardenDescription);
        testGarden.setId(testGardenId);
        when(gardenService.addGarden(any(Garden.class))).thenReturn(testGarden);
        result = mockGardenFormControllerMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", testGardenName)
                        .param("location", testStreetNumberName)
                        .param("suburb", testSuburb)
                        .param("city", testCity)
                        .param("country", testCountry)
                        .param("postcode", testPostcode)
                        .param("size", testSize)
                        .param("description", testGardenDescription)
                        .param("redirect", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }
    @Then("The error message for invalid description comes up.")
    public void the_error_message_for_invalid_description_comes_up() {
        MockHttpServletRequest request = result.getRequest();
        String descriptionError = (String) request.getAttribute("descriptionError");
        assertTrue(
                "Garden description must be less than 512 characters <br/>".equals(descriptionError) ||
                        "Description must be 512 characters or less and contain some text <br/>".equals(descriptionError));
    }

    @When("I submit the create form with {string} including bad words for the garden description")
    public void i_submit_the_create_form_with_including_bad_words_for_the_garden_description(String description) throws Exception {
        testGardenDescription = description;
        testGarden = new Garden(testGardenName, testStreetNumberName, testSuburb, testCity, testCountry, testPostcode, testSize, testGardener, testGardenDescription);
        testGarden.setId(testGardenId);
        when(gardenService.addGarden(any(Garden.class))).thenReturn(testGarden);
        result = mockGardenFormControllerMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", testGardenName)
                        .param("location", testStreetNumberName)
                        .param("suburb", testSuburb)
                        .param("city", testCity)
                        .param("country", testCountry)
                        .param("postcode", testPostcode)
                        .param("size", testSize)
                        .param("description", testGardenDescription)
                        .param("redirect", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Then("The error message for inappropriate words comes up.")
    public void the_error_message_for_inappropriate_words_comes_up() {
        MockHttpServletRequest request = result.getRequest();
        String descriptionError = (String) request.getAttribute("descriptionError");
        assertEquals("The description does not match the language standards of the app.", descriptionError);
    }



}


