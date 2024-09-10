package nz.ac.canterbury.seng302.gardenersgrove.unit.service;


import nz.ac.canterbury.seng302.gardenersgrove.repository.IdentifiedPlantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class PlantIdentificationServiceTest {


    private IdentifiedPlantRepository identifiedPlantRepository;

    @BeforeEach
    public void setUp() {
        identifiedPlantRepository = Mockito.mock(IdentifiedPlantRepository.class);
    }


    @Test
    public void getAllPlantNames_ReturnsAllPlantNames() {
        List<String> expectedPlantNameList = Arrays.asList( "Sun Flower", "Rose", "Arthurchoke", "Lavender", "Rosemary");
        when(identifiedPlantRepository.getAllPlantNames()).thenReturn(expectedPlantNameList);
        List<String> actualPlantNameList = identifiedPlantRepository.getAllPlantNames();

        assertEquals(expectedPlantNameList, actualPlantNameList);
    }
    @Test
    public void UserHasNoPlantInCollection_TryToGetAllPlantNames_ReturnsNull() {
        List<String> expectedPlantNameList = List.of();
        when(identifiedPlantRepository.getAllPlantNames()).thenReturn(expectedPlantNameList);
        List<String> actualPlantNameList = identifiedPlantRepository.getAllPlantNames();

        assertTrue(actualPlantNameList.isEmpty());
    }


    @Test
    public void getAllSpeciesScientificNames_ReturnsAllSpeciesScientificNames() {
        List<String> expectedSpeciesScientificNameList = Arrays.asList( "the First Scientific Name", "the Second Scientific Name", "the Third Scientific Name", "the Fourth Scientific Name", "the Scientific Name");
        when(identifiedPlantRepository.getAllSpeciesScientificName()).thenReturn(expectedSpeciesScientificNameList);
        List<String> actualSpeciesScientificNameList = identifiedPlantRepository.getAllSpeciesScientificName();

        assertEquals(actualSpeciesScientificNameList, expectedSpeciesScientificNameList);

    }

    @Test
    public void UserHasNoPlantInCollection_TryToGetAllSpeciesScientificNames_ReturnsNull() {
        List<String> expectedSpeciesScientificNameList = List.of();
        when(identifiedPlantRepository.getAllSpeciesScientificName()).thenReturn(expectedSpeciesScientificNameList);
        List<String> actualSpeciesScientificNameList = identifiedPlantRepository.getAllSpeciesScientificName();

        assertTrue(actualSpeciesScientificNameList.isEmpty());
    }


}
