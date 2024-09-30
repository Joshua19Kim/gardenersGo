package nz.ac.canterbury.seng302.gardenersgrove.cucumber.step_definitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Badge;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.BadgeRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.IdentifiedPlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.BadgeService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.IdentifiedPlantService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class EarnRegionBadgeFeature {
    @Autowired
    private GardenerFormService gardenerFormService;
    @Autowired
    private IdentifiedPlantRepository identifiedPlantRepository;
    @Autowired
    private IdentifiedPlantService identifiedPlantService;
    @Autowired
    private BadgeRepository badgeRepository;
    @Autowired
    private BadgeService badgeService;
    private Gardener testGardener;

    @Before("@U7009C")
    public void setUp() {
        badgeRepository.deleteAll();
    }

    @After("@U7009C")
    public void tearDown() {
        identifiedPlantRepository.deleteAll();
        badgeRepository.deleteAll();
    }

    @Given("I have collected {int} regions,")
    public void i_have_collected_regions(Integer regionCount) {
        List<String> regions = List.of("Southland", "Otago", "Canterbury", "West Coast", "Northland",
                "Tasman", "Waikato", "Wellington", "Taranaki", "Manawatu-Wanganui", "Marlborough",
                "Hawke's Bay", "Gisborne", "Bay of Plenty", "Auckland", "Nelson", "Chatham Islands");

        testGardener = gardenerFormService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        for(int i = 0; i < regionCount; i++) {
            IdentifiedPlant plant = new IdentifiedPlant("Plant " + i,
                    0.88,
                    List.of("Sunflower", "Rose"),
                    "5414641",
                    "https://example.com/sunflower.jpg",
                    "https://example.com/sunflower.jpg",
                    "Helianthus",
                    "annuus",
                    testGardener);

            plant.setRegion(regions.get(i));
            identifiedPlantService.saveIdentifiedPlantDetails(plant);
        }
    }

    @Then("I will be shown the region badge with name {string}")
    public void i_will_be_shown_the_region_badge_with_name(String expectedName) throws Exception {
        Optional<Badge> badge = badgeService.getMyBadgeByName(expectedName, testGardener.getId());
        Assertions.assertTrue(badge.isPresent());
        Assertions.assertEquals(expectedName, badge.get().getName());
    }
}
