package nz.ac.canterbury.seng302.gardenersgrove.integration.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.GardenVisit;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenVisitRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenVisitService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@Import(GardenVisitService.class)
public class GardenVisitServiceTest {
    @Autowired
    private GardenRepository gardenRepository;
    @Autowired
    private GardenerFormRepository gardenerFormRepository;
    @Autowired
    private LostPasswordTokenRepository lostPasswordTokenRepository;
    @Autowired
    private GardenVisitRepository gardenVisitRepository;
    private GardenVisitService gardenVisitService;
    private GardenService gardenService;
    private Gardener testGardener;

    @BeforeEach
    public void setUp() {
        gardenService = new GardenService(gardenRepository);
        GardenerFormService gardenerFormService = new GardenerFormService(gardenerFormRepository, lostPasswordTokenRepository);
        gardenVisitService = new GardenVisitService(gardenVisitRepository);

        testGardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        testGardener.setId(1L);
        gardenerFormService.addGardener(testGardener);

    }

    @AfterEach
    public void tearDown() {
        gardenVisitRepository.deleteAllGardenVisitsByGardenerId(testGardener.getId());
        gardenRepository.deleteAll();
        gardenerFormRepository.deleteAll();
    }

    @Test
    public void GardenVisitAdded_OneGarden_GardenVisitReturnedAndShowGardenInRecentList() {
        Garden testBotanicalGarden = new Garden("Botanical",
                "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", testGardener, "");
        gardenService.addGarden(testBotanicalGarden);

        LocalDateTime currentTime = LocalDateTime.now();
        GardenVisit gardenVisit = gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime));

        Assertions.assertEquals(gardenVisit.getGardener(), testGardener);
        Assertions.assertEquals(gardenVisit.getGarden(), testBotanicalGarden);
        Assertions.assertEquals(gardenVisit.getAccessTime(), currentTime);

        List<Garden> recentGardens = gardenVisitService.findRecentGardensByGardenerId(testGardener.getId());

        Assertions.assertTrue(recentGardens.contains(testBotanicalGarden));
    }

    @Test
    public void GardenVisitAdded_TwoDifferentGardens_GardenVisitReturnedAndShowGardensInRecentList() {
        Garden testBotanicalGarden = new Garden("Botanical",
                "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", testGardener, "");
        gardenService.addGarden(testBotanicalGarden);
        Garden testRoseGarden = new Garden("Rose Garden",
                "22 Kirkwood street", null, "Christchurch", "New Zealand", null, "25", testGardener, "");
        gardenService.addGarden(testRoseGarden);

        LocalDateTime currentTime = LocalDateTime.now();
        GardenVisit botanicalGardenVisit = gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime));
        GardenVisit roseGardenVisit = gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testRoseGarden, currentTime));

        Assertions.assertEquals(botanicalGardenVisit.getGardener(), testGardener);
        Assertions.assertEquals(botanicalGardenVisit.getGarden(), testBotanicalGarden);
        Assertions.assertEquals(botanicalGardenVisit.getAccessTime(), currentTime);

        Assertions.assertEquals(roseGardenVisit.getGardener(), testGardener);
        Assertions.assertEquals(roseGardenVisit.getGarden(), testRoseGarden);
        Assertions.assertEquals(roseGardenVisit.getAccessTime(), currentTime);

        List<Garden> recentGardens = gardenVisitService.findRecentGardensByGardenerId(testGardener.getId());
        Assertions.assertTrue(recentGardens.contains(testBotanicalGarden));
        Assertions.assertTrue(recentGardens.contains(testRoseGarden));
    }

    @Test
    public void GardenVisitAdded_ThreeDifferentGardens_GardenVisitReturnedAndShowGardensInRecentList() {
        Garden testBotanicalGarden = new Garden("Botanical",
                "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", testGardener, "");
        gardenService.addGarden(testBotanicalGarden);
        Garden testRoseGarden = new Garden("Rose Garden",
                "22 Kirkwood street", null, "Christchurch", "New Zealand", null, "25", testGardener, "");
        gardenService.addGarden(testRoseGarden);
        Garden testTulipGarden = new Garden("Tulip Garden",
                "100 Lovely street", null, "Busan", "South Korea", null, "3000", testGardener, "");
        gardenService.addGarden(testTulipGarden);


        LocalDateTime currentTime = LocalDateTime.now();
        GardenVisit botanicalGardenVisit = gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testBotanicalGarden, currentTime));
        GardenVisit roseGardenVisit = gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testRoseGarden, currentTime));
        GardenVisit tulipGardenVisit = gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testTulipGarden, currentTime));


        Assertions.assertEquals(botanicalGardenVisit.getGardener(), testGardener);
        Assertions.assertEquals(botanicalGardenVisit.getGarden(), testBotanicalGarden);
        Assertions.assertEquals(botanicalGardenVisit.getAccessTime(), currentTime);

        Assertions.assertEquals(roseGardenVisit.getGardener(), testGardener);
        Assertions.assertEquals(roseGardenVisit.getGarden(), testRoseGarden);
        Assertions.assertEquals(roseGardenVisit.getAccessTime(), currentTime);

        Assertions.assertEquals(tulipGardenVisit.getGardener(), testGardener);
        Assertions.assertEquals(tulipGardenVisit.getGarden(), testTulipGarden);
        Assertions.assertEquals(tulipGardenVisit.getAccessTime(), currentTime);

        List<Garden> recentGardens = gardenVisitService.findRecentGardensByGardenerId(testGardener.getId());
        Assertions.assertTrue(recentGardens.contains(testBotanicalGarden));
        Assertions.assertTrue(recentGardens.contains(testRoseGarden));
        Assertions.assertTrue(recentGardens.contains(testTulipGarden));

    }



}