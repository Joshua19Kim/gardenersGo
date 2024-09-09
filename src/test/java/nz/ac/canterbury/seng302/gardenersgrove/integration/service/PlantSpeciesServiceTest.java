package nz.ac.canterbury.seng302.gardenersgrove.integration.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.PlantSpecies;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantSpeciesRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantSpeciesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class PlantSpeciesServiceTest {

    private PlantSpeciesService plantSpeciesService;

    @Autowired
    private PlantSpeciesRepository plantSpeciesRepository;

    private int totalPlantSpecies;

    private List<PlantSpecies> allPlantSpecies;

    private PlantSpecies expectedPlantSpecies;

    @BeforeEach
    public void setUp() {
        plantSpeciesService = new PlantSpeciesService(plantSpeciesRepository);
        totalPlantSpecies = 21;
        allPlantSpecies = new ArrayList<>();
        for(int i = 0; i < totalPlantSpecies; i++) {
            PlantSpecies plantSpecies = new PlantSpecies("Plant" + i, i, "randomImage" + i + ".jpg");
            allPlantSpecies.add(plantSpecies);
            if(i == 0) {
               expectedPlantSpecies = plantSpeciesService.addPlantSpecies(plantSpecies);
            } else {
                plantSpeciesService.addPlantSpecies(plantSpecies);
            }

        }
    }

    @AfterEach
    public void tearDown() {
        plantSpeciesRepository.deleteAll();
    }

    @Test
    public void SpeciesAdded_ValidSpecies_InDatabase() {
        PlantSpecies plantSpecies = new PlantSpecies("Apple", 2, "random.jpg");
        PlantSpecies returnedPlantSpecies = plantSpeciesService.addPlantSpecies(plantSpecies);
        Assertions.assertEquals(plantSpecies, returnedPlantSpecies);
    }

    @Test
    public void GetAllSpecies_ValidSpecies_AllSpeciesFound() {
        List<PlantSpecies> actualPlantSpecies = plantSpeciesService.getAllPlantSpecies();
        Assertions.assertEquals(allPlantSpecies, actualPlantSpecies);
    }

    @Test
    public void GetSpeciesById_ValidId_SpeciesReturned() {
        Optional<PlantSpecies> actualPlantSpecies = plantSpeciesService.getPlantSpecies(expectedPlantSpecies.getId());
        Assertions.assertEquals(expectedPlantSpecies, actualPlantSpecies.get());

    }

    @Test
    public void plantSpeciesPaginatedRequested_PageNumberAndSizeGiven_plantSpeciesReturned() {
        int pageNo = 0;
        int pageSize = 12;
        Page<PlantSpecies> plantSpeciesPage = plantSpeciesService.getAllPlantSpeciesPaginated(pageNo, pageSize);
        Assertions.assertEquals(Math.ceil((double) totalPlantSpecies /pageSize), plantSpeciesPage.getTotalPages());

        Assertions.assertEquals(allPlantSpecies.subList(0, 12), plantSpeciesPage.getContent());
    }

    @Test
    public void plantSpeciesPaginatedRequested_PageNumberOutOfRange_NoplantSpeciesReturned() {
        int pageNo = 3;
        int pageSize = 12;
        Page<PlantSpecies> plantSpeciesPage = plantSpeciesService.getAllPlantSpeciesPaginated(pageNo, pageSize);
        Assertions.assertEquals(Math.ceil((double) totalPlantSpecies /pageSize), plantSpeciesPage.getTotalPages());
        Assertions.assertTrue(plantSpeciesPage.getContent().isEmpty());
    }

    @Test
    public void plantSpeciesPaginatedRequested_OnePlantSpeciesPerPage_plantSpeciesReturned() {
        int pageNo = 1;
        int pageSize = 1;
        Page<PlantSpecies> plantSpeciesPage = plantSpeciesService.getAllPlantSpeciesPaginated(pageNo, pageSize);
        Assertions.assertEquals(totalPlantSpecies, plantSpeciesPage.getTotalPages());
        Assertions.assertEquals(pageSize, plantSpeciesPage.getContent().size());
    }


}
