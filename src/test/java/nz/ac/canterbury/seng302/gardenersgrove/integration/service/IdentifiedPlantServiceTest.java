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
class IdentifiedPlantServiceTest {

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

    private int totalSpecies;

    @BeforeEach
    public void setUp() {
        identifiedPlantService = new IdentifiedPlantService(identifiedPlantRepository);
        gardenerFormService = new GardenerFormService(gardenerFormRepository, lostPasswordTokenRepository);
        gardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        gardener = gardenerFormService.addGardener(gardener);
        totalPlants = 12;
        totalSpecies = 3;
        for(int i = 0; i< totalPlants; i++) {
            IdentifiedPlant identifiedPlant = new IdentifiedPlant("Plant Name" + i, gardener);
            if(i % (totalPlants / totalSpecies) == 0) {
                identifiedPlant.setSpeciesScientificNameWithoutAuthor("Species" + i);
            }
            identifiedPlantService.saveIdentifiedPlantDetails(identifiedPlant);
        }
    }

    @AfterEach
    public void tearDown() {
        identifiedPlantRepository.deleteAll();
        gardenerFormRepository.deleteAll();

    }

    @Test
    void getCollectionCountTest() {
        int count = identifiedPlantService.getCollectionPlantCount(gardener.getId());
        Assertions.assertEquals(totalPlants, count);
    }

    @Test
    void getSpeciesCountTest() {
        int count = identifiedPlantService.getSpeciesCount(gardener.getId());
        Assertions.assertEquals(totalSpecies, count);
    }
}
