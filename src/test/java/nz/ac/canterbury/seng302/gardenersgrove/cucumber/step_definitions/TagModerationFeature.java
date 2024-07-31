package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.GardenControllers.GardenDetailsController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import nz.ac.canterbury.seng302.gardenersgrove.util.WordFilter;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
@SpringBootTest
public class TagModerationFeature {
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
    @Mock
    private LocationService locationService;
    private MockMvc mockMvcGardenDetailsController;
    private Garden garden;
    private Tag tag;
    private Gardener gardener;


    @Before("@U22")
    public void setUp() throws IOException, InterruptedException {
        gardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        garden = new Garden("Test garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "9999", "1.0", gardener, "");
        gardenService = Mockito.mock(GardenService.class);
        gardenerFormService = Mockito.mock(GardenerFormService.class);
        requestService = Mockito.mock(RequestService.class);
        relationshipService = Mockito.mock(RelationshipService.class);
        weatherService = Mockito.mock(WeatherService.class);
        tagService = Mockito.mock(TagService.class);
        locationService = Mockito.mock(LocationService.class);
        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        when(response.body()).thenReturn("test");
        when(locationService.sendRequest(any())).thenReturn(response);

        Authentication authentication = Mockito.mock(Authentication.class);
        Authentication auth = new UsernamePasswordAuthenticationToken("testgardener@gmail.com", "Password1!");
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testgardener@gmail.com");
        when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
        when(gardenerFormService.findByEmail(any())).thenReturn(Optional.of(gardener));

        GardenDetailsController gardenDetailsController = new GardenDetailsController(gardenService,
                gardenerFormService,
                relationshipService,
                requestService,
                weatherService,
                tagService,
                locationService);

        mockMvcGardenDetailsController = MockMvcBuilders.standaloneSetup(gardenDetailsController).build();
    }

    @Given("I am adding a valid tag")
    public void i_am_adding_a_valid_tag() {
        tag = new Tag("My tag", garden);
        when(gardenService.getGarden(anyLong())).thenReturn(Optional.of(garden));
        when(tagService.addTag(any())).thenReturn(tag);
    }

    @Given("I add an inappropriate tag")
    public void the_submitted_tag_is_evaluated_for_appropriateness() {
        tag = new Tag("Fuck", garden);
        when(tagService.addTag(any())).thenReturn(tag);
        when(tagService.addBadWordCount(gardener)).thenAnswer(invocation -> {
            Gardener gardener = invocation.getArgument(0);
            gardener.setBadWordCount(gardener.getBadWordCount() + 1);
            return "Submitted tag fails moderation requirements";
        });
    }

    @When("I confirm the tag")
    public void i_confirm_the_tag() throws Exception {
        mockMvcGardenDetailsController
                .perform(
                        (MockMvcRequestBuilders.post("/gardens/addTag")
                                .param("tag-input", tag.getName())
                                .param("gardenId", "1")
                                .with(csrf())))
                .andReturn();
    }
    @Then("The tag is checked for offensive or inappropriate words")
    public void the_tag_is_checked_for_offensive_or_inappropriate_words() {
        assertEquals(false, WordFilter.doesContainBadWords(tag.getName()));
    }

    @Then("the tag is not added to the list of user-defined tags")
    public void the_tag_is_not_added_to_the_list_of_user_defined_tags() {
        Mockito.verify(tagService, times(0)).addTag(any());
    }

    @Then("the users bad word counter is incremented by one")
    public void the_users_bad_word_counter_is_incremented_by_one() {
        assertEquals(1, gardener.getBadWordCount());
    }
}
