package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.controller.BrowseGardensController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.FollowerService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class BrowseGardensByTagFeature {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GardenerFormService gardenerFormService;

    @Autowired
    private GardenService gardenService;

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private GardenRepository gardenRepository;

    @Autowired
    private FollowerService followerService;

    private List<String> allTags;

    private ResultActions resultActions;

    private String tag;

    @Given("there is a garden with tags")
    public void there_is_a_garden_with_tags() {
        Gardener gardener = gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        Garden garden = new Garden("Test garden", "99 test address", null, "Christchurch", "New Zealand", null, "9999", gardener, "");
        gardenService.addGarden(garden);

        allTags = new ArrayList<>();
        allTags.add("berries");
        allTags.add("healthy");
        allTags.add("herbs");
        for(String tag: allTags) {
            tagService.addTag(new Tag(tag, garden));
        }
        BrowseGardensController browseGardensControllerSpy = spy(new BrowseGardensController(gardenService, gardenerFormService, tagService, followerService));
        mockMvc = standaloneSetup(browseGardensControllerSpy).build();
        doNothing().when(browseGardensControllerSpy).setSearchTerm(anyString());
    }

    @After("@U24")
    public void tearDown() {
        tagRepository.deleteAll();
        gardenRepository.deleteAll();
    }



    @Given("I am on the browse gardens page")
    public void i_am_on_the_browse_gardens_page() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/browseGardens"))
                .andExpect(model().attribute("allTags", allTags))
                .andExpect(view().name("browseGardensTemplate"))
                .andExpect(status().isOk());

    }
    @Given("I type out the tag {string}")
    public void i_type_out_the_tag(String tag) {
        this.tag = tag;
    }
    @When("I press the enter key")
    public void i_press_the_enter_key() throws Exception{
        resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/browseGardens/addTag")
                .param("tag-input", tag)
                .with(csrf()));
    }
    @Then("the tag is added to my current selection")
    public void the_tag_is_added_to_my_current_selection() throws Exception{
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/browseGardens"))
                .andExpect(flash().attribute("tags", List.of(tag)));
    }
    @Then("the text field is cleared")
    public void the_text_field_is_cleared() {
        MvcResult mvcResult = resultActions.andReturn();
        Assertions.assertFalse(mvcResult.getFlashMap().containsKey("tag"));
    }

    @Then("the tag is not added to my current selection")
    public void the_tag_is_not_added_to_my_current_selection() throws Exception{
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/browseGardens"))
                .andExpect(flash().attribute("tags", List.of()));
    }
    @Then("the text field is not cleared")
    public void the_text_field_is_not_cleared() throws Exception{
        resultActions.andExpect(flash().attribute("tag", tag));
    }
    @Then("an error message on the browse gardens page tells me {string}")
    public void an_error_message_on_the_browse_gardens_page_tells_me(String errorMessage) throws Exception {
        resultActions.andExpect(flash().attribute("tagValid", errorMessage));
    }


}
