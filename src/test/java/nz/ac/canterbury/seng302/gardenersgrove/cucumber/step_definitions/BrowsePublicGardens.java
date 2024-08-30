package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Follower;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FollowerService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.FlashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class BrowsePublicGardens {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GardenRepository gardenRepository;

    @Autowired
    private GardenService gardenService;
    @Autowired
    private FollowerService followerService;
    private ResultActions resultActions;
    private String searchTerm;
    private Gardener gardener;
    private Garden gardenToFollow;
    private List<String> allTags = new ArrayList<>();


    @Autowired
    private GardenerFormService gardenerFormService;


    @Given("there is a garden with the name {string}")
    public void thereIsAGardenWithTheName(String gardenName) {
        gardener = gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
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
        List<Garden> allGardens = gardenService.getGardensByGardenerId(gardener.getId());
        Garden garden = gardenService.getGardensByGardenerId(gardener.getId()).stream().filter(garden1 -> garden1.getName().equals(gardenName)).findFirst().get();
        followerService.addfollower(new Follower(gardener.getId(), garden.getId()));
        gardenToFollow = garden;
    }

    @Given("I want to follow a garden with the name {string}")
    public void I_want_to_follow_a_garden_with_the_name(String gardenName) {
        gardenToFollow = gardenService.getGardensByGardenerId(gardener.getId()).stream().filter(garden1 -> garden1.getName().equals(gardenName)).findFirst().get();
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



}
