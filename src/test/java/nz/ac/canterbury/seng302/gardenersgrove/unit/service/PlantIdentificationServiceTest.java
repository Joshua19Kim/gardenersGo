package nz.ac.canterbury.seng302.gardenersgrove.unit.service;


import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.IdentifiedPlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@DataJpaTest
@Transactional
public class PlantIdentificationServiceTest {

    private  Gardener testGardener;
    private GardenerFormService gardenerFormService;
    @Autowired
    private GardenerFormRepository gardenerFormRepository;
    private IdentifiedPlantRepository identifiedPlantRepository;
    @Autowired
    private LostPasswordTokenRepository lostPasswordTokenRepository;

    @BeforeEach
    public void setUp() {
        testGardener = new Gardener("Michael", "Scott", LocalDate.of(1980, 1, 1),
                "testEmail@gmail.com", "password");
        gardenerFormService = new GardenerFormService(gardenerFormRepository, lostPasswordTokenRepository);
        gardenerFormService.addGardener(testGardener);
        identifiedPlantRepository = Mockito.mock(IdentifiedPlantRepository.class);
    }


    @Test
    public void getAllPlantNames_ReturnsAllPlantNames() {
        List<String> expectedPlantNameList = Arrays.asList( "Sun Flower", "Rose", "Arthurchoke", "Lavender", "Rosemary");
        when(identifiedPlantRepository.getAllPlantNames(testGardener.getId())).thenReturn(expectedPlantNameList);
        List<String> actualPlantNameList = identifiedPlantRepository.getAllPlantNames(testGardener.getId());

        assertEquals(expectedPlantNameList, actualPlantNameList);
    }
    @Test
    public void UserHasNoPlantInCollection_TryToGetAllPlantNames_ReturnsNull() {
        List<String> expectedPlantNameList = List.of();
        when(identifiedPlantRepository.getAllPlantNames(testGardener.getId())).thenReturn(expectedPlantNameList);
        List<String> actualPlantNameList = identifiedPlantRepository.getAllPlantNames(testGardener.getId());

        assertTrue(actualPlantNameList.isEmpty());
    }


    @Test
    public void getAllSpeciesScientificNames_ReturnsAllSpeciesScientificNames() {
        List<String> expectedSpeciesScientificNameList = Arrays.asList( "the First Scientific Name", "the Second Scientific Name", "the Third Scientific Name", "the Fourth Scientific Name", "the Scientific Name");
        when(identifiedPlantRepository.getAllSpeciesScientificName(testGardener.getId())).thenReturn(expectedSpeciesScientificNameList);
        List<String> actualSpeciesScientificNameList = identifiedPlantRepository.getAllSpeciesScientificName(testGardener.getId());

        assertEquals(actualSpeciesScientificNameList, expectedSpeciesScientificNameList);

    }

    @Test
    public void UserHasNoPlantInCollection_TryToGetAllSpeciesScientificNames_ReturnsNull() {
        List<String> expectedSpeciesScientificNameList = List.of();
        when(identifiedPlantRepository.getAllSpeciesScientificName(testGardener.getId())).thenReturn(expectedSpeciesScientificNameList);
        List<String> actualSpeciesScientificNameList = identifiedPlantRepository.getAllSpeciesScientificName(testGardener.getId());

        assertTrue(actualSpeciesScientificNameList.isEmpty());
    }


}
