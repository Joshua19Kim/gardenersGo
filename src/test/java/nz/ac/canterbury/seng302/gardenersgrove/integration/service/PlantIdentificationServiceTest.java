package nz.ac.canterbury.seng302.gardenersgrove.integration.service;


import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.IdentifiedPlant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.IdentifiedPlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantIdentificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@Import(PlantIdentificationService.class)
public class PlantIdentificationServiceTest {

    @Autowired
    private IdentifiedPlantRepository identifiedPlantRepository;
    @Autowired
    private PlantIdentificationService plantIdentificationService;
    private GardenerFormService gardenerFormService;
    @Autowired
    private GardenerFormRepository gardenerFormRepository;
    @Autowired
    private LostPasswordTokenRepository lostPasswordTokenRepository;
    private Gardener testGardener;


    @BeforeEach
    public void setUp() {
        testGardener = new Gardener("Michael", "Scott", LocalDate.of(1980, 1, 1),
                "testEmail@gmail.com", "password");
        gardenerFormService = new GardenerFormService(gardenerFormRepository, lostPasswordTokenRepository);
        gardenerFormService.addGardener(testGardener);

    }

    @Test
    public void UserHasSomePlantsInCollection_TrysToGetAllPlantNames_ReturnAllPlantNames() {
        IdentifiedPlant FirstTestIdentifiedPlant = new IdentifiedPlant(
                "Helianthus annuus",
                0.88,
                List.of("Sunflower", "Rose"),
                "5414641",
                "https://example.com/sunflower.jpg",
                "https://example.com/sunflower.jpg",
                "Helianthus",
                "annuus"
                , testGardener
        );
        FirstTestIdentifiedPlant.setName("my sunflower");
        FirstTestIdentifiedPlant.setDescription("my beautiful sunflower");
        IdentifiedPlant SecondTestIdentifiedPlant = new IdentifiedPlant(
                "Dahlia × cultorum Thorsrud & Reisaeter",
                0.44,
                List.of("test1", "test2"),
                "5414641",
                "https://bs.plantnet.org/image/o/d3858ddac8102b471365e0c46c8594307b1b9ad5",
                "https://bs.plantnet.org/image/o/d3858ddac8102b471365e0c46c8594307b1b9ad5",
                "Dahlia × cultorum Thorsrud & Reisaeter",
                "Dahlia × cultorum Thorsrud & Reisaeter"
                , testGardener
        );
        SecondTestIdentifiedPlant.setName("my red flower");
        SecondTestIdentifiedPlant.setDescription("my beautiful red flower");
        IdentifiedPlant ThirdTestIdentifiedPlant = new IdentifiedPlant(
                "Capsicum annuum L.",
                0.33,
                List.of("test3", "test4"),
                "5414641",
                "https://bs.plantnet.org/image/o/22f08fb18ae072c254b0ad40d1e287c3d266cca7",
                "https://bs.plantnet.org/image/o/22f08fb18ae072c254b0ad40d1e287c3d266cca7",
                "Capsicum annuum L.",
                "Capsicum annuum L."
                , testGardener
        );
        ThirdTestIdentifiedPlant.setName("my red capsicum");
        ThirdTestIdentifiedPlant.setDescription("my beautiful red capsicum");

        identifiedPlantRepository.save(FirstTestIdentifiedPlant);
        identifiedPlantRepository.save(SecondTestIdentifiedPlant);
        identifiedPlantRepository.save(ThirdTestIdentifiedPlant);


        List<String> actualPlantNameList = plantIdentificationService.getAllPlantNames();

        assertEquals(Arrays.asList(
                ThirdTestIdentifiedPlant.getName(),
                SecondTestIdentifiedPlant.getName(),
                FirstTestIdentifiedPlant.getName()),
                actualPlantNameList
        );
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    public void UserHasNoPlantsInCollection_TrysToGetAllPlantNames_ReturnNothing() {
        List<String> actualPlantNameList = plantIdentificationService.getAllPlantNames();
        assertTrue(actualPlantNameList.isEmpty());
    }

    @Test
    public void UserHasSomePlantsInCollection_TrysToGetAllSpeciesScientificNames_ReturnAllSpeciesScientificNames() {
        IdentifiedPlant FirstTestIdentifiedPlant = new IdentifiedPlant(
                "Helianthus annuus",
                0.88,
                List.of("Sunflower", "Rose"),
                "5414641",
                "https://example.com/sunflower.jpg",
                "https://example.com/sunflower.jpg",
                "Helianthus",
                "annuus"
                , testGardener
        );
        FirstTestIdentifiedPlant.setName("my sunflower");
        FirstTestIdentifiedPlant.setDescription("my beautiful sunflower");
        IdentifiedPlant SecondTestIdentifiedPlant = new IdentifiedPlant(
                "Dahlia × cultorum Thorsrud & Reisaeter",
                0.44,
                List.of("test1", "test2"),
                "5414641",
                "https://bs.plantnet.org/image/o/d3858ddac8102b471365e0c46c8594307b1b9ad5",
                "https://bs.plantnet.org/image/o/d3858ddac8102b471365e0c46c8594307b1b9ad5",
                "Dahlia × cultorum Thorsrud & Reisaeter",
                "Dahlia × cultorum Thorsrud & Reisaeter"
                , testGardener
        );
        SecondTestIdentifiedPlant.setName("my red flower");
        SecondTestIdentifiedPlant.setDescription("my beautiful red flower");
        IdentifiedPlant ThirdTestIdentifiedPlant = new IdentifiedPlant(
                "Capsicum annuum L.",
                0.33,
                List.of("test3", "test4"),
                "5414641",
                "https://bs.plantnet.org/image/o/22f08fb18ae072c254b0ad40d1e287c3d266cca7",
                "https://bs.plantnet.org/image/o/22f08fb18ae072c254b0ad40d1e287c3d266cca7",
                "Capsicum annuum L.",
                "Capsicum annuum L."
                , testGardener
        );
        ThirdTestIdentifiedPlant.setName("my red capsicum");
        ThirdTestIdentifiedPlant.setDescription("my beautiful red capsicum");

        identifiedPlantRepository.save(FirstTestIdentifiedPlant);
        identifiedPlantRepository.save(SecondTestIdentifiedPlant);
        identifiedPlantRepository.save(ThirdTestIdentifiedPlant);


        List<String> actualPlantNameList = plantIdentificationService.getAllSpeciesScientificNames();

        assertEquals(Arrays.asList(
                        ThirdTestIdentifiedPlant.getSpeciesScientificNameWithoutAuthor(),
                        SecondTestIdentifiedPlant.getSpeciesScientificNameWithoutAuthor(),
                        FirstTestIdentifiedPlant.getSpeciesScientificNameWithoutAuthor()),
                actualPlantNameList
        );
    }

    @Test
    @WithMockUser("testEmail@gmail.com")
    public void UserHasNoPlantsInCollection_TrysToGetAllSpeciesScientificNames_ReturnNothing() {
        List<String> actualSpeciesScientificNameList = plantIdentificationService.getAllSpeciesScientificNames();
        assertTrue(actualSpeciesScientificNameList.isEmpty());
    }








}
