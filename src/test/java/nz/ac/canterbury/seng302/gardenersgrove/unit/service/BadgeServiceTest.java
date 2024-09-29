package nz.ac.canterbury.seng302.gardenersgrove.unit.service;


import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.*;
import nz.ac.canterbury.seng302.gardenersgrove.repository.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.BadgeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
public class BadgeServiceTest {

    @Autowired
    private BadgeRepository badgeRepository;
    private BadgeService badgeService;
    private final Gardener testGardener1 = new Gardener("test", "test", LocalDate.of(2010, 10, 10), "email@doe.com", "Password1!");

    private final Gardener testGardener2 = new Gardener("test", "test", LocalDate.of(2010, 10, 10), "email@doe.com", "Password1!");

    private final Gardener testGardener3 = new Gardener("test", "test", LocalDate.of(2010, 10, 10), "email@doe.com", "Password1!");


    @BeforeEach
    public void setUp() {
        badgeService = new BadgeService(badgeRepository);

        testGardener1.setId(1L);
        testGardener2.setId(2L);
        testGardener3.setId(3L);

        badgeService.addBadge(new Badge("1st Plant Found", LocalDate.of(2024, 1, 7), BadgeType.PLANTS, testGardener1, "/images/badges/1PlantBadge.png"));
        badgeService.addBadge(new Badge("10th Plant Found", LocalDate.of(2024, 1, 8), BadgeType.PLANTS, testGardener1, "/images/badges/10PlantBadge.png"));
        badgeService.addBadge(new Badge("25th Plant Found", LocalDate.of(2024, 1, 9), BadgeType.PLANTS, testGardener1, "/images/badges/25PlantBadge.png"));
        badgeService.addBadge(new Badge("1st Plant Found", LocalDate.of(2024, 2, 7), BadgeType.PLANTS, testGardener1, "/images/badges/1SpeciesBadge.png"));
        badgeService.addBadge(new Badge("1st Plant Found", LocalDate.of(2024, 3, 7), BadgeType.PLANTS, testGardener1, "/images/badges/10SpeciesBadge.png"));
        badgeService.addBadge(new Badge("1st Plant Found", LocalDate.of(2024, 4, 7), BadgeType.PLANTS, testGardener1, "/images/badges/25SpeciesBadge.png"));

        badgeService.addBadge(new Badge("1st Plant Found", LocalDate.now(), BadgeType.PLANTS, testGardener2, "/images/badges/1PlantBadge.png"));
        badgeService.addBadge(new Badge("10th Plant Found", LocalDate.now(), BadgeType.PLANTS, testGardener2, "/images/badges/10PlantBadge.png"));

    }

    @Test
    void GardenerHasMoreThanFiveBadges_GetBadges_OnlyFiveReturned() {
        List<Badge> recentBadges = badgeService.getMyRecentBadges(1L);
        assertEquals(5, recentBadges.size());
    }

    @Test
    void GardenerHasLessThanFiveBadges_GetBadges_AllReturned() {
        List<Badge> recentBadges = badgeService.getMyRecentBadges(2L);
        assertEquals(2, recentBadges.size());
    }

    @Test
    void GardenerHasNoBadges_GetBadges_NoneReturned() {
        List<Badge> recentBadges = badgeService.getMyRecentBadges(3L);
        assertEquals(0, recentBadges.size());
    }

    @Test
    void GardenerHasMoreThanFiveBadges_GetBadges_MostRecentBadgeFirst() {
        List<Badge> recentBadges = badgeService.getMyRecentBadges(1L);
        assertFalse(recentBadges.get(0).getDateEarned().isBefore(recentBadges.get(1).getDateEarned()));
    }

}
