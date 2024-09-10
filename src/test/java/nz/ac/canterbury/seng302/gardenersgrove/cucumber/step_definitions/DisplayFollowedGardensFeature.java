package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Follower;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.FollowerRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FollowerService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
    private FollowerRepository followerRepository;
    @Autowired
    private GardenerFormService gardenerFormService;
    private ResultActions resultActions;
    private Gardener gardener;

    @Given("I am following a public garden with the name {string}")
    public void i_am_following_a_public_garden_with_the_name(String gardenName) {
        gardener = gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        Garden gardenToFollow = new Garden(gardenName, "99 test address", null, "Christchurch", "New Zealand", null, "9999", gardener, "");
        gardenToFollow.setIsGardenPublic(true);
        gardenService.addGarden(gardenToFollow);

        Follower followedGarden = new Follower(gardener.getId(), gardenToFollow.getId());
        followerService.addfollower(followedGarden);
    }

    @Given("there are no followed gardens")
    public void there_are_no_followed_gardens() {
        followerRepository.deleteAll();
        gardener = gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        Assertions.assertTrue(followerService.findAllGardens(gardener.getId()).isEmpty());
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

    @Then("I see a message saying {string}")
    public void i_see_a_message_saying(String errorMessage) throws Exception {
        resultActions
                .andExpect(model().attribute("errorMessage", errorMessage));
    }

}
