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
    private Gardener testGardener;
    private Garden testGarden;

    @BeforeEach
    public void setUp() {
        GardenService gardenService = new GardenService(gardenRepository);
        GardenerFormService gardenerFormService = new GardenerFormService(gardenerFormRepository, lostPasswordTokenRepository);
        gardenVisitService = new GardenVisitService(gardenVisitRepository);

        testGardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        gardenerFormService.addGardener(testGardener);


        testGarden = new Garden("Botanical",
                "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", testGardener, "");
        gardenService.addGarden(testGarden);
    }

    @AfterEach
    public void tearDown() {
        gardenRepository.deleteAll();
        gardenerFormRepository.deleteAll();
    }

    @Test
    public void GardenVisitAdded_ValidInputs_GardenVisitReturned() {
        LocalDateTime currentTime = LocalDateTime.now();
        GardenVisit gardenVisit = gardenVisitService.addGardenVisit(new GardenVisit(testGardener, testGarden, currentTime));
        Assertions.assertEquals(gardenVisit.getGardener(), testGardener);
        Assertions.assertEquals(gardenVisit.getGarden(), testGarden);
        Assertions.assertEquals(gardenVisit.getAccessTime(), currentTime);
    }
}