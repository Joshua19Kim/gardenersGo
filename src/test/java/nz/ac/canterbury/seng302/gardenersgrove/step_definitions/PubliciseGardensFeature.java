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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    @WithMockUser
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
        testGardenDescription = "testing description function!";


        gardenerFormService = Mockito.mock(GardenerFormService.class);
        gardenService = Mockito.mock(GardenService.class);
//        authentication = Mockito.mock(Authentication.class);

//        when(authentication.getName()).thenReturn("testgardener@gmail.com");




        GardenFormController gardenFormController = new GardenFormController(gardenService, gardenerFormService,
                new RelationshipService(Mockito.mock(RelationshipRepository.class), gardenerFormService),
                new RequestService(),
                Mockito.mock(WeatherService.class),
                new TagService(Mockito.mock(TagRepository.class)));

        mockMvc = MockMvcBuilders.standaloneSetup(gardenFormController).build();
    }

    @WithMockUser(username = "testgardener@gmail.com")
    @Given("I click Create New Garden")
    public void i_click_create_new_garden() {
        List<Authority> userRoles = new ArrayList<>();
        testGardener.setUserRoles(userRoles);
        testGardener.setId(1L);
        when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(testGardener));
    }

    @WithMockUser(username = "testgardener@gmail.com")
    @When("I add valid description of the garden, and I submit the form")
    public void i_add_valid_description_of_the_garden_and_i_submit_the_form() throws Exception {
        testGardenDescription = "testing description function!";
        editTestGardenDescription = "new description!";
        testGarden = new Garden(testGardenName, testStreetNumberName, testSuburb, testCity, testCountry, testPostcode, testSize, testGardener, testGardenDescription);
        testGarden.setId(testGardenId);
        when(gardenService.addGarden(any(Garden.class))).thenReturn(testGarden);
        auth = new UsernamePasswordAuthenticationToken("testgardener@gmail.com", "Password1!");
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(MockMvcRequestBuilders.post("/gardens/form")
                        .param("name", testGardenName)
                        .param("location", testStreetNumberName)
                        .param("suburb", testSuburb)
                        .param("city", testCity)
                        .param("country", testCountry)
                        .param("postcode", testPostcode)
                        .param("size", testSize)
                        .param("description", editTestGardenDescription)
                        .param("redirect", "")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/gardens/details?gardenId=1"));
    }

    @WithMockUser(username = "testgardener@gmail.com")
    @Then("The description is persisted.")
    public void the_description_is_persisted() {
        assertEquals(editTestGardenDescription, testGarden.getDescription());
    }
}
