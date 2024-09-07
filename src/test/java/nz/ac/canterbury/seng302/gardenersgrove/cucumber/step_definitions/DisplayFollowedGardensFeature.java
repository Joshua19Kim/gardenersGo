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
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Collections;
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

    @Given("I am following a public garden with the name {string}")
    public void i_am_following_a_public_garden_with_the_name(String gardenName) {
        Gardener testGardener = new Gardener("test", "test", null, "jane@doe.com", "Password1!");
        gardenerFormService.addGardener(testGardener);

        Garden gardenToFollow = new Garden("follow me", "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        gardenToFollow.setIsGardenPublic(true);
        gardenService.addGarden(gardenToFollow);

        Garden garden = new Garden(gardenName, "99 test address", null, "Christchurch", "New Zealand", null, "9999", testGardener, "");
        garden.setIsGardenPublic(true);
        gardenService.addGarden(garden);

        Follower followedGarden = new Follower(testGardener.getId(), gardenToFollow.getId());
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

    @Then("I see {string} shown in the followed gardens section list")
    public void i_see_a_list_of_all_the_public_gardens_i_am_following(String gardenName) throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("followedGardenList"))
                .andExpect(model().attribute("followedGardenList", Matchers.hasItem(Matchers.hasProperty("name", Matchers.equalTo(gardenName)))));
    }

    @Then("I can view details of {string} by selecting it from the list")
    public void i_can_view_details_of_garden_by_selecting_it_from_the_list(String gardenName) throws Exception {
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("gardensTemplate"))
                .andExpect(model().attributeExists("followedGardenList"))
                .andExpect(model().attribute("followedGardenList", Matchers.hasItem(
                        Matchers.hasProperty("name", Matchers.equalTo(gardenName))
                )));
    }

    @Then("I see a message saying {string}")
    public void i_see_a_message_saying(String errorMessage) throws Exception {
        resultActions
                .andExpect(model().attribute("You are not following any gardens yet.", errorMessage));
    }

}
