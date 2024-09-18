package nz.ac.canterbury.seng302.gardenersgrove.integration.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.BadgeRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.IdentifiedPlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.BadgeService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.IdentifiedPlantService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

@DataJpaTest
public class IdentifiedPlantServiceTest {

    @Autowired
    private IdentifiedPlantRepository identifiedPlantRepository;

    @Autowired
    private GardenerFormRepository gardenerFormRepository;

    @Autowired
    private LostPasswordTokenRepository lostPasswordTokenRepository;

    private IdentifiedPlantService identifiedPlantService;
    private Gardener gardener;

    private GardenerFormService gardenerFormService;

    private int totalPlants;

    @BeforeEach
    public void setUp() {
        identifiedPlantService = new IdentifiedPlantService(identifiedPlantRepository);
        gardenerFormService = new GardenerFormService(gardenerFormRepository, lostPasswordTokenRepository);
        gardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        gardener = gardenerFormService.addGardener(gardener);
        totalPlants = 12;
        for(int i = 0; i< totalPlants; i++) {
            IdentifiedPlant identifiedPlant = new IdentifiedPlant("Plant Name" + i, gardener);
            identifiedPlantService.saveIdentifiedPlantDetails(identifiedPlant);
        }
    }

    @AfterEach
    public void tearDown() {
        identifiedPlantRepository.deleteAll();
        gardenerFormRepository.deleteAll();

    }

    @Test
    public void getCollectionCountTest() {
        int count = identifiedPlantService.getCollectionPlantCount(gardener.getId());
        Assertions.assertEquals(totalPlants, count);
    }
}
