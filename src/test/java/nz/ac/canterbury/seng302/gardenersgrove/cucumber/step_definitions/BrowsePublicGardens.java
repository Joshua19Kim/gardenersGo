package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Follower;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.service.FollowerService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.FlashMap;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class BrowsePublicGardens {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GardenService gardenService;
    @Autowired
    private FollowerService followerService;
    @Autowired
    private GardenerFormService gardenerFormService;
    private ResultActions resultActions;
    private String searchTerm;
    private Garden garden;
    private Gardener gardener;
    private Gardener testGardener;
    private Garden gardenToFollow;

    @Given("There is a test gardener which has a garden that the current user can follow")
    public void setUpFollower() {
        testGardener = new Gardener("test", "test", null, "jane@doe.com", "Password1!");
        gardenToFollow = new Garden("follow me", "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        gardenToFollow.setIsGardenPublic(true);
        gardenerFormService.addGardener(testGardener);
        gardenService.addGarden(gardenToFollow);
    }

    @Given("there is a garden with the name {string}")
    public void thereIsAGardenWithTheName(String gardenName) {
        gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).ifPresent(gardener1 -> gardener = gardener1);
        Garden garden = new Garden(gardenName, "99 test address", null, "Christchurch", "New Zealand", null, "9999", gardener, "");
        garden.setIsGardenPublic(true);
        gardenService.addGarden(garden);
    }


    @Given("I am on the browse gardens page to search")
    public void iAmOnTheBrowseGardensPageToSearch() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/browseGardens"))
                .andExpect(view().name("browseGardensTemplate"))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Given("I input the search term {string}")
    public void iInputTheSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @Given("I am following the garden {string}")
    public void i_am_following_the_garden(String gardenName) {
        Garden garden = new Garden(gardenName, "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        garden.setIsGardenPublic(true);
        gardenService.addGarden(garden);
        followerService.addFollower(new Follower(gardener.getId(), garden.getId()));
        gardenToFollow = garden;
    }

    @Given("I want to follow a garden with the name {string}")
    public void I_want_to_follow_a_garden_with_the_name(String gardenName) {
        Garden garden = new Garden(gardenName, "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        garden.setIsGardenPublic(true);
        gardenService.addGarden(garden);
        gardenToFollow = garden;
    }

    @When("I press the search button")
    public void iPressTheSearchButton() throws Exception {
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/browseGardens")
                .param("searchTerm", searchTerm)
                .with(csrf()));
    }

    @Then("The gardens with matching results are shown")
    public void theGardensWithMatchingResultsAreShown() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("browseGardensTemplate"));

        MvcResult mvcResult = resultActions.andReturn();
        String content = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("Apple Orchard"), "Garden 'Apple Orchard' not found in search results");
    }

    @When("I click on the follow button")
    public void i_click_on_the_follow_button() throws Exception {

        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/follow")
                .param("pageNo", "1")
                .param("gardenToFollow", gardenToFollow.getId().toString())
                .with(csrf()));
    }

    @Then("I am now following the garden")
    public void i_am_now_following_the_garden() {
        Optional<Follower> followerOptional = followerService.findFollower(gardener.getId(), gardenToFollow.getId());
        assertTrue(followerOptional.isPresent());
    }

    @Then("I see a notification telling me I am now following that garden")
    public void i_see_a_notification_telling_me_i_am_now_following_the_garden() {
        MvcResult result = resultActions.andReturn();
        FlashMap flashMap = result.getFlashMap();
        assertEquals("You are now following " + gardenToFollow.getName(), flashMap.get("gardenFollowUpdate"));
    }

    @Then("I see a notification telling me I am no longer following that garden")
    public void i_see_a_notification_telling_me_i_am_no_longer_following_the_garden() {
        MvcResult result = resultActions.andReturn();
        FlashMap flashMap = result.getFlashMap();
        assertEquals("You are no longer following " + gardenToFollow.getName(), flashMap.get("gardenFollowUpdate"));

    }

    @Then("I am no longer following the garden")
    public void i_am_no_longer_following_the_garden() {
        Optional<Follower> followerOptional = followerService.findFollower(gardener.getId(), gardenToFollow.getId());
        assertTrue(followerOptional.isEmpty());
    }

    @Given("I have a public garden with the name {string} that has at least one follower")
    public void i_have_a_public_garden_with_the_name_that_has_at_least_one_follower(String gardenName) {
        gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).ifPresent(gardener1 -> gardener = gardener1);
        garden = new Garden(gardenName, "99 test address", null, "Christchurch", "New Zealand", null, "9999", gardener, "");
        garden.setIsGardenPublic(true);
        gardenService.addGarden(garden);

        testGardener = new Gardener("test", "test", null, "jane@doe.com", "Password1!");
        gardenerFormService.addGardener(testGardener);

        followerService.addFollower(new Follower(testGardener.getId(), garden.getId()));
    }

    @When("I navigate to the garden details page")
    public void i_navigate_to_the_garden_details_page() throws Exception {
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/gardens/details")
                        .param("gardenId", garden.getId().toString())
                        .with(SecurityMockMvcRequestPostProcessors.user(gardener.getEmail())));
    }

    @Then("I see the followers section showing the followers of the garden")
    public void i_see_the_followers_section_showing_the_followers_of_the_garden() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("I have a public garden with the name {string} that has at no followers")
    public void i_have_a_public_garden_with_the_name_that_has_at_no_followers(String gardenName) {
        gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).ifPresent(gardener1 -> gardener = gardener1);
        garden = new Garden(gardenName, "99 test address", null, "Christchurch", "New Zealand", null, "9999", gardener, "");
        garden.setIsGardenPublic(true);
        gardenService.addGarden(garden);
    }

    @Then("It should display a message indicating {string}")
    public void it_should_display_a_message_indicating(String message) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
}
