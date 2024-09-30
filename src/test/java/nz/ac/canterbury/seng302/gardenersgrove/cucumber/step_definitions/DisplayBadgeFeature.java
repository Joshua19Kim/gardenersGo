package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Badge;
import nz.ac.canterbury.seng302.gardenersgrove.entity.BadgeType;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.BadgeRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.BadgeService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class DisplayBadgeFeature {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GardenService gardenService;
    @Autowired
    private GardenerFormService gardenerFormService;
    @Autowired
    private BadgeService badgeService;
    @Autowired
    private BadgeRepository badgeRepository;
    private ResultActions resultActions;
    private Gardener gardener;
    private List<Badge> badgeList = new ArrayList<>();

    @Before("@U7010")
    public void setUp() {
        badgeRepository.deleteAll();
    }
    @After("@U7010")
    public void tearDown() {
        badgeRepository.deleteAll();
    }

    @Given("I have earned a badge")
    public void i_have_earned_a_badge() {
        badgeList.clear();
        gardener = gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        Optional<Badge> badgeOptional =  badgeService.checkPlantBadgeToBeAdded(gardener, 1);
        Badge savedBadge = badgeService.addBadge(badgeOptional.get());
        badgeList.add(savedBadge);
    }
    @When("I view my profile")
    public void i_view_my_profile() throws Exception {
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .with(SecurityMockMvcRequestPostProcessors.user(gardener.getEmail())));
    }
    @Then("The earned badges should be prominently displayed on my profile, including badge images and names.")
    public void the_earned_badges_should_be_prominently_displayed_on_my_profile_including_badge_images_and_names() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attributeExists("earnedBadges"));
        MvcResult mvcResult = resultActions.andReturn();
        List<Badge> badges = (List<Badge>) mvcResult.getModelAndView().getModel().get("earnedBadges");
        IntStream.range(0, badges.size()).forEach(i -> {
            assertEquals(badges.get(i).getName(), badgeList.get(i).getName());
        });
    }

    @Given("I have more than five badges")
    public void i_have_more_than_five_badges() {
        badgeList.clear();
        List<String> names = List.of(
                "1st Plant Found",
                "10th Plant Found",
                "25th Plant Found",
                "50th Plant Found",
                "100th Plant Found",
                "10th Species Found"
        );
        gardener = gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        IntStream.range(0, 6).forEach(i -> {
            Badge badge = new Badge(names.get(i), LocalDate.now(), BadgeType.PLANTS, gardener, "hehe");
            badgeService.addBadge(badge);
            badgeList.add(badge);
        });
    }

    @Then("I should see the five most recently acquired badges")
    public void i_should_see_the_five_most_recently_acquired_badges() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attributeExists("earnedBadges"));
        MvcResult mvcResult = resultActions.andReturn();
        List<Badge> badges = (List<Badge>) mvcResult.getModelAndView().getModel().get("earnedBadges");
        IntStream.range(0, 5).forEach(i -> {
            assertEquals(badges.get(i).getName(), badgeList.get(i).getName());
        });
    }

    @When("I view all badges")
    public void i_view_all_badges() throws Exception {
        resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/badges")
                .with(SecurityMockMvcRequestPostProcessors.user(gardener.getEmail())));
    }

    @Then("I should be shown all badges sectioned into those that are achieved and those that are not")
    public void i_should_be_shown_all_badges_sectioned_into_those_that_are_achieved_and_those_that_are_not() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("allBadges"))
                .andExpect(model().attributeExists("lockedBadgeNames"))
                .andExpect(model().attributeExists("earnedBadges"));

        MvcResult mvcResult = resultActions.andReturn();
        List<Badge> unlockedBadges = (List<Badge>) mvcResult.getModelAndView().getModel().get("earnedBadges");
        HashMap<String, String> lockedBadges = (HashMap<String, String>) mvcResult.getModelAndView().getModel().get("lockedBadgeNames");

        assertEquals(6, unlockedBadges.size());
        assertEquals(8, lockedBadges.size());
    }

    @Given("I have no badges")
    public void i_have_no_badges() {
        badgeList.clear();
        gardener = gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
    }
    @Then("I should see nothing in the badges section")
    public void i_should_see_nothing_in_the_badges_section() throws Exception {
        resultActions.andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attributeExists("earnedBadges"));
        MvcResult mvcResult = resultActions.andReturn();
        List<Badge> badges = (List<Badge>) mvcResult.getModelAndView().getModel().get("earnedBadges");
        assertEquals(0, badges.size());
    }


}
