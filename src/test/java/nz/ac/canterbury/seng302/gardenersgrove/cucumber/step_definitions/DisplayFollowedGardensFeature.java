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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.FlashMap;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class DisplayFollowedGardensFeature {
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
    private Gardener currentUser;
    private Gardener otherUser;

    @Given("I am following a public garden with the name {string} with id {long}")
    public void i_am_following_a_public_garden_with_the_name(String gardenName, Long id) {
        Gardener currentUser = new Gardener("Test", "Gardener", LocalDate.of(2000, 1, 1), "test@test.com", "Password1!");
        Gardener otherUser = new Gardener("Test", "Gardener 2", LocalDate.of(2000, 1, 1), "test2@test.com", "Password1!");
        currentUser.setId(1L);
        otherUser.setId(2L);

        gardenerFormService.addGardener(currentUser);
        gardenerFormService.addGardener(otherUser);

        Garden gardenToFollow = new Garden(gardenName, "99 test address", null, "Christchurch", "New Zealand", null, "9999", otherUser, "");
        gardenToFollow.setIsGardenPublic(true);
        gardenService.addGarden(gardenToFollow);
        gardenToFollow.setId(id);

        Follower followedGarden = new Follower(currentUser.getId(), gardenToFollow.getId());
        followerService.addfollower(followedGarden);
    }

    @Given("there are no followed gardens")
    public void there_are_no_followed_gardens() {
        Gardener currentUser = new Gardener("Test", "Gardener", LocalDate.of(2000, 1, 1), "test@test.com", "Password1!");
        currentUser.setId(1L);
        gardenerFormService.addGardener(currentUser);

        when(followerService.findAllGardens(currentUser.getId())).thenReturn(Collections.emptyList());
    }

    @When("I navigate to the followed gardens section of the My Gardens page")
    public void i_navigate_to_the_followed_gardens_section_of_the_my_gardens_page() throws Exception{
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/gardens"))
                .andExpect(view().name("gardensTemplate"))
                .andExpect(status().isOk());
    }

    @Then("I see a list of all the public gardens I am following")
    public void i_see_a_list_of_all_the_public_gardens_i_am_following() {
        MvcResult result = resultActions.andReturn();
        FlashMap flashMap = result.getFlashMap();
        assertEquals("Followed Gardens", flashMap.get("followedGardenList"));
//        assertEquals("You are now following " + gardenToFollow.getName(), flashMap.get("gardenFollowUpdate"));

    }

    @Then("I can view details of {string} by selecting it from the list")
    public void i_can_view_details_of_garden_by_selecting_it_from_the_list(String gardenName) {
        MvcResult result = resultActions.andReturn();
        FlashMap flashMap = result.getFlashMap();
        assertEquals("Followed Gardens", flashMap.get("followedGardenList"));
    }

    @Then("I see a message saying {string}")
    public void i_see_a_message_saying(String errorMessage) {
        MvcResult result = resultActions.andReturn();
        FlashMap flashMap = result.getFlashMap();
        assertEquals("Followed Gardens", flashMap.get("followedGardenList"));
    }

}
