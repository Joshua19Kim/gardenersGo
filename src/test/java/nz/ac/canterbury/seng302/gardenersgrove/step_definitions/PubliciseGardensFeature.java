package nz.ac.canterbury.seng302.gardenersgrove.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenFormController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Authority;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.AuthorityFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.RelationshipRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.ValidityChecker;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Long.parseLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class PubliciseGardensFeature {

    @Autowired
    private AuthorityFormRepository authorityFormRepository;

    private MockMvc mockMvc;

    @MockBean
    private GardenService gardenService;

    @MockBean
    private GardenerFormService gardenerFormService;

    @MockBean
    private AuthenticationManager authenticationManager;

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
    private  Authentication auth;

    @Before
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
        auth = new UsernamePasswordAuthenticationToken("testgardener@gmail.com", "Password1!");
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);
        gardenerFormService = Mockito.mock(GardenerFormService.class);
        when(authentication.getName()).thenReturn("testgardener@gmail.com");

        GardenFormController gardenFormController = new GardenFormController(gardenService, gardenerFormService,
                new RelationshipService(Mockito.mock(RelationshipRepository.class), gardenerFormService),
                new RequestService(), Mockito.mock(WeatherService.class),
                new TagService(Mockito.mock(TagRepository.class)));

        mockMvc = MockMvcBuilders.standaloneSetup(gardenFormController).build();
    }

    //AC2,3
    @Given("I on Create New Garden form")
    public void i_on_create_new_garden_form() {
        List<Authority> userRoles = new ArrayList<>();
        testGardener.setUserRoles(userRoles);
        testGardener.setId(1L);
        when(gardenerFormService.findByEmail(anyString())).thenReturn(Optional.of(testGardener));
    }

    //AC2
    @When("I add valid description of the garden, and I submit the create form")
    public void i_add_valid_description_of_the_garden_and_i_submit_the_create_form() throws Exception {
        testGardenDescription = "testing description function!";
        testGarden = new Garden(testGardenName, testStreetNumberName, testSuburb, testCity, testCountry, testPostcode, testSize, testGardener, testGardenDescription);
        testGarden.setId(testGardenId);
        when(gardenService.addGarden(any(Garden.class))).thenReturn(testGarden);
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
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
    //AC3
    @When("I do not add any description, and I submit the create form")
    public void i_do_not_add_any_description_and_i_submit_the_create_form() throws Exception {
        testGardenDescription = "";
        testGarden = new Garden(testGardenName, testStreetNumberName, testSuburb, testCity, testCountry, testPostcode, testSize, testGardener, testGardenDescription);
        testGarden.setId(testGardenId);
        when(gardenService.addGarden(any(Garden.class))).thenReturn(testGarden);
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
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

    @When("I add valid description of the garden, and I submit the edit form")
    public void i_add_valid_description_of_the_garden_and_i_submit_the_edit_form() throws Exception {
        testGardenDescription = "Original description!";
        editTestGardenDescription = "Updated description!!!!!!!!!!!!!!!!!!";
        testGarden = new Garden(testGardenName, testStreetNumberName, testSuburb, testCity, testCountry, testPostcode, testSize, testGardener, testGardenDescription);
        testGarden.setId(testGardenId);
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(testGarden));
        when(gardenService.addGarden(any(Garden.class))).thenReturn(testGarden);
        when(gardenService.getGardensByGardenerId(any())).thenReturn(List.of(testGarden));
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/edit")
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

    @When("I do not add any description, and I submit the edit form")
    public void i_do_not_add_any_description_and_i_submit_the_edit_form() throws Exception {
        testGardenDescription = "Original description!";
        editTestGardenDescription = "";
        testGarden = new Garden(testGardenName, testStreetNumberName, testSuburb, testCity, testCountry, testPostcode, testSize, testGardener, testGardenDescription);
        testGarden.setId(testGardenId);
        when(gardenService.getGarden(1L)).thenReturn(Optional.of(testGarden));
        when(gardenService.addGarden(any(Garden.class))).thenReturn(testGarden);
        when(gardenService.getGardensByGardenerId(any())).thenReturn(List.of(testGarden));
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/edit")
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


    @When("I add invalid {string} of the garden, and I submit the create form")
    public void i_add_invalid_of_the_garden_and_i_submit_the_create_form(String description) throws Exception {
        testGardenDescription = description;
        testGarden = new Garden(testGardenName, testStreetNumberName, testSuburb, testCity, testCountry, testPostcode, testSize, testGardener, testGardenDescription);
        testGarden.setId(testGardenId);
        when(gardenService.addGarden(any(Garden.class))).thenReturn(testGarden);
        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
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
                .andExpect(status().isOk());
    }
//    @Then("The error message comes up.")
//    public void the_error_message_comes_up() {
////        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
//        Model model = Mockito.mock(Model.class);
//        verify(model, Mockito.times(1)).addAttribute("descriptionError", eq(anyString()));
////        List<String> errorMessage = stringCaptor.getAllValues();
////        assertEquals("Description must be 512 characters or less and contain some text", errorMessage);
//
//    }




}


