package nz.ac.canterbury.seng302.gardenersgrove.integration.service;


import nz.ac.canterbury.seng302.gardenersgrove.entity.Badge;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.BadgeRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.BadgeService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BadgeServiceTest {

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private GardenerFormRepository gardenerFormRepository;

    @Autowired
    private LostPasswordTokenRepository lostPasswordTokenRepository;

    private BadgeService badgeService;

    private Gardener gardener;

    private GardenerFormService gardenerFormService;

    @BeforeEach
    public void setUp() {
        badgeService = new BadgeService(badgeRepository);
        gardenerFormService = new GardenerFormService(gardenerFormRepository, lostPasswordTokenRepository);
        gardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        gardener = gardenerFormService.addGardener(gardener);
    }

    @AfterEach
    public void tearDown() {
        badgeRepository.deleteAll();
        gardenerFormRepository.deleteAll();

    }


    @ParameterizedTest
    @CsvSource(value = {
            "1, 1st Plant Found",
            "10, 10th Plant Found",
            "25, 25th Plant Found",
            "50, 50th Plant Found",
            "100, 100th Plant Found",
    })
    void checkPlantBadgeToBeAddedTest(int plantCount, String expectedName) {
        badgeService.checkPlantBadgeToBeAdded(gardener, plantCount);
        Optional<Badge> expectedBadge = badgeService.getMyBadgeByName(expectedName, gardener.getId());
        assertTrue(expectedBadge.isPresent());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11, 24, 49, 99, 101
    })
    void checkPlantBadgeToBeAddedTest(int plantCount) {
        badgeService.checkPlantBadgeToBeAdded(gardener, plantCount);
        List<Badge> badges = badgeService.getMyBadges(gardener.getId());
        assertTrue(badges.isEmpty());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, 1st Species Found",
            "10, 10th Species Found",
            "25, 25th Species Found",
            "50, 50th Species Found",
            "100, 100th Species Found",
    })
    void checkSpeciesBadgeToBeAddedTest(int speciesCount, String expectedName) {
        badgeService.checkSpeciesBadgeToBeAdded(gardener, speciesCount);
        Optional<Badge> expectedBadge = badgeService.getMyBadgeByName(expectedName, gardener.getId());
        assertTrue(expectedBadge.isPresent());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11, 24, 49, 99, 101
    })
    void checkSpeciesBadgeToBeAddedTest(int speciesCount) {
        badgeService.checkSpeciesBadgeToBeAdded(gardener, speciesCount);
        List<Badge> badges = badgeService.getMyBadges(gardener.getId());
        assertTrue(badges.isEmpty());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, 1st Region Found",
            "5, 5th Region Found",
            "10, 10th Region Found",
            "17, 17th Region Found",
    })
    void checkRegionBadgeToBeAddedTest(int regionCount, String expectedName) {
        badgeService.checkRegionBadgeToBeAdded(gardener, regionCount);
        Optional<Badge> expectedBadge = badgeService.getMyBadgeByName(expectedName, gardener.getId());
        assertTrue(expectedBadge.isPresent());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 11, 24, 49, 99, 101
    })
    void checkRegionBadgeToBeAddedTest(int regionCount) {
        badgeService.checkSpeciesBadgeToBeAdded(gardener, regionCount);
        List<Badge> badges = badgeService.getMyBadges(gardener.getId());
        assertTrue(badges.isEmpty());
    }
}

