package nz.ac.canterbury.seng302.gardenersgrove.integration.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import nz.ac.canterbury.seng302.gardenersgrove.controller.AllBadgesController;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers= {AllBadgesController.class})
public class AllBadgesControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BadgeService badgeService;
    @MockBean
    private GardenService gardenService;
    @MockBean
    private GardenerFormService gardenerFormService;

    private Gardener testGardener;

    @BeforeEach
    void setUp() {
        testGardener = new Gardener("Test", "Gardener", LocalDate.of(2024, 4, 1), "testgardener@gmail.com", "Password1!");
        testGardener.setId(1L);
        Mockito.when(gardenerFormService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(testGardener));
    }

    @Test
    @WithMockUser
    void noBadgesUnlocked_AllBadgesRequested_BadgePageReturned() throws Exception {
        HashMap<String, String> lockedBadgeNames = new HashMap<>() {{
            put("1st Plant Found", "/images/badges/1PlantBadge.png");
            put("10th Plant Found", "/images/badges/10PlantBadge.png");
            put("25th Plant Found", "/images/badges/25PlantBadge.png");
            put("50th Plant Found", "/images/badges/50PlantBadge.png");
            put("100th Plant Found", "/images/badges/100PlantBadge.png");
            put("1st Species Found", "/images/badges/1SpeciesBadge.png");
            put("10th Species Found", "/images/badges/10SpeciesBadge.png");
            put("25th Species Found", "/images/badges/25SpeciesBadge.png");
            put("50th Species Found", "/images/badges/50SpeciesBadge.png");
            put("100th Species Found", "/images/badges/100SpeciesBadge.png");
            put("1st Region Found", "/images/badges/1RegionBadge.png");
            put("5th Region Found", "/images/badges/5RegionBadge.png");
            put("10th Region Found", "/images/badges/10RegionBadge.png");
            put("17th Region Found", "/images/badges/17RegionBadge.png");
        }};

        List<Badge> unlockedBadges = null;

        Mockito.when(badgeService.getMyLockedBadgeNames(Mockito.anyLong())).thenReturn(lockedBadgeNames);
        Mockito.when(badgeService.getMyBadges(Mockito.anyLong())).thenReturn(unlockedBadges);
        mockMvc.perform(MockMvcRequestBuilders.get("/badges"))
                .andExpect(view().name("allBadges"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("lockedBadgeNames", lockedBadgeNames))
                .andExpect(model().attribute("earnedBadges", unlockedBadges));
    }

    @Test
    @WithMockUser
    void someBadgesUnlocked_AllBadgesRequested_BadgePageReturned() throws Exception {
        HashMap<String, String> lockedBadgeNames = new HashMap<>() {{
            put("25th Plant Found", "/images/badges/25PlantBadge.png");
            put("50th Plant Found", "/images/badges/50PlantBadge.png");
            put("100th Plant Found", "/images/badges/100PlantBadge.png");
            put("10th Species Found", "/images/badges/10SpeciesBadge.png");
            put("25th Species Found", "/images/badges/25SpeciesBadge.png");
            put("50th Species Found", "/images/badges/50SpeciesBadge.png");
            put("100th Species Found", "/images/badges/100SpeciesBadge.png");
            put("5th Region Found", "/images/badges/5RegionBadge.png");
            put("10th Region Found", "/images/badges/10RegionBadge.png");
            put("17th Region Found", "/images/badges/17RegionBadge.png");
        }};
        List<Badge> unlockedBadges = List.of(
                new Badge("1st Plant Found", LocalDate.now(), BadgeType.PLANTS, testGardener, "/images/badges/1PlantBadge.png"),
                new Badge("10th Plant Found", LocalDate.now(), BadgeType.PLANTS, testGardener, "/images/badges/10PlantBadge.png"),
                new Badge("1st Species Found", LocalDate.now(), BadgeType.SPECIES, testGardener, "/images/badges/1SpeciesBadge.png"),
                new Badge("1st Region Found", LocalDate.now(), BadgeType.REGION, testGardener, "/images/badges/1RegionBadge.png")
        );

        Mockito.when(badgeService.getMyLockedBadgeNames(Mockito.anyLong())).thenReturn(lockedBadgeNames);
        Mockito.when(badgeService.getMyBadges(Mockito.anyLong())).thenReturn(unlockedBadges);

        mockMvc.perform(MockMvcRequestBuilders.get("/badges"))
                .andExpect(view().name("allBadges"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("lockedBadgeNames", lockedBadgeNames))
                .andExpect(model().attribute("earnedBadges", unlockedBadges));
    }

    @Test
    @WithMockUser
    void allBadgesUnlocked_AllBadgesRequested_BadgePageReturned() throws Exception {
        HashMap<String, String> lockedBadgeNames = null;
        List<Badge> unlockedBadges = List.of(
                new Badge("1st Plant Found", LocalDate.now(), BadgeType.PLANTS, testGardener, "/images/badges/1PlantBadge.png"),
                new Badge("10th Plant Found", LocalDate.now(), BadgeType.PLANTS, testGardener, "/images/badges/10PlantBadge.png"),
                new Badge("25th Plant Found", LocalDate.now(), BadgeType.PLANTS, testGardener, "/images/badges/25PlantBadge.png"),
                new Badge("50th Plant Found", LocalDate.now(), BadgeType.PLANTS, testGardener, "/images/badges/50PlantBadge.png"),
                new Badge("100th Plant Found", LocalDate.now(), BadgeType.PLANTS, testGardener, "/images/badges/100PlantBadge.png"),
                new Badge("1st Species Found", LocalDate.now(), BadgeType.SPECIES, testGardener, "/images/badges/1SpeciesBadge.png"),
                new Badge("10th Species Found", LocalDate.now(), BadgeType.SPECIES, testGardener, "/images/badges/10SpeciesBadge.png"),
                new Badge("25th Species Found", LocalDate.now(), BadgeType.SPECIES, testGardener, "/images/badges/25SpeciesBadge.png"),
                new Badge("50th Species Found", LocalDate.now(), BadgeType.SPECIES, testGardener, "/images/badges/50SpeciesBadge.png"),
                new Badge("100th Species Found", LocalDate.now(), BadgeType.SPECIES, testGardener, "/images/badges/100SpeciesBadge.png"),
                new Badge("1st Region Found", LocalDate.now(), BadgeType.REGION, testGardener, "/images/badges/1RegionBadge.png"),
                new Badge("5th Region Found", LocalDate.now(), BadgeType.REGION, testGardener, "/images/badges/5RegionBadge.png"),
                new Badge("10th Region Found", LocalDate.now(), BadgeType.REGION, testGardener, "/images/badges/10RegionBadge.png"),
                new Badge("17th Region Found", LocalDate.now(), BadgeType.REGION, testGardener, "/images/badges/17RegionBadge.png")
        );

        Mockito.when(badgeService.getMyLockedBadgeNames(Mockito.anyLong())).thenReturn(lockedBadgeNames);
        Mockito.when(badgeService.getMyBadges(Mockito.anyLong())).thenReturn(unlockedBadges);

        mockMvc.perform(MockMvcRequestBuilders.get("/badges"))
                .andExpect(view().name("allBadges"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("lockedBadgeNames", lockedBadgeNames))
                .andExpect(model().attribute("earnedBadges", unlockedBadges));
    }

}