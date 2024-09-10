package nz.ac.canterbury.seng302.gardenersgrove.integration.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.*;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(PlantService.class)
public class PlantServiceTest {

    @Autowired
    private PlantRepository plantRepository;
    @Autowired
    private IdentifiedPlantRepository identifiedPlantRepository;
    @Autowired
    private GardenRepository gardenRepository;
    @Autowired
    private GardenerFormRepository gardenerFormRepository;
    @Autowired
    private MainPageLayoutRepository mainPageLayoutRepository;
    Gardener testGardener;
    @BeforeEach
    public void setUp() {
        identifiedPlantRepository.deleteAll();
        plantRepository.deleteAll();
        gardenRepository.deleteAll();
        mainPageLayoutRepository.deleteAll();
        gardenerFormRepository.deleteAll();

        testGardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
    }

    @Test
    public void PlantAdded_ValidInputs_PlantSavedToRepository() {
        Garden garden = new Garden("Botanical","20 Marquess street",null,"ChristChurch","New Zealand", "8870", testGardener, "");
        PlantService plantService = new PlantService(new PlantRepository() {
            @Override
            public Optional<Plant> findById(long id) {
                return Optional.empty();
            }

            @Override
            public List<Plant> findAll() {
                return null;
            }

            @Override
            public List<Plant> findTop3ByGardenGardenerIdOrderByIdDesc(Long gardenerId) {
                return null;
            }

            @Override
            public <S extends Plant> S save(S entity) {
                Assertions.assertEquals(entity.getName(), "Flower");
                Assertions.assertEquals(entity.getCount(), "2");
                Assertions.assertEquals(entity.getDescription(), "Rose");
                Assertions.assertEquals(entity.getDatePlanted(), "08/02/2024");
                Assertions.assertEquals(entity.getGarden(), garden);
                return entity;
            }

            @Override
            public <S extends Plant> Iterable<S> saveAll(Iterable<S> entities) {
                return null;
            }

            @Override
            public Optional<Plant> findById(Long aLong) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(Long aLong) {
                return false;
            }

            @Override
            public Iterable<Plant> findAllById(Iterable<Long> longs) {
                return null;
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public void deleteById(Long aLong) {

            }

            @Override
            public void delete(Plant entity) {

            }

            @Override
            public void deleteAllById(Iterable<? extends Long> longs) {

            }

            @Override
            public void deleteAll(Iterable<? extends Plant> entities) {

            }

            @Override
            public void deleteAll() {

            }
        });
        plantService.addPlant(new Plant("Flower","2", "Rose", "08/02/2024", garden));
    }

    @Test
    public void PlantAdded_ValidInputs_PlantReturned() {
        PlantService plantService = new PlantService(plantRepository);
        Garden garden = new Garden("Botanical","20 Marquess street",null,"ChristChurch","New Zealand", "8870", testGardener, "");
        Plant plant = plantService.addPlant(new Plant("Flower","2", "Rose", "08/02/2024", garden));
        Assertions.assertEquals(plant.getName(), "Flower");
        Assertions.assertEquals(plant.getCount(), "2");
        Assertions.assertEquals(plant.getDescription(), "Rose");
        Assertions.assertEquals(plant.getDatePlanted(), "08/02/2024");
        Assertions.assertEquals(plant.getGarden(), garden);
    }

    @Test
    public void FindNewestPlantsByGardenerId_ValidInputs_ReturnsNewestPlants() {
        gardenerFormRepository.save(testGardener);

        Garden testGarden = new Garden("Botanical", "20 Marquess street", null, "ChristChurch", "New Zealand", "8870", testGardener, "");
        gardenRepository.save(testGarden);

        Plant oldestPlant = new Plant("Old Flower", "1", "Old Description", "01/01/2023", testGarden);
        Plant middlePlant = new Plant("Middle Flower", "2", "Middle Description", "01/01/2024", testGarden);
        Plant newestPlant = new Plant("New Flower", "3", "New Description", "01/01/2025", testGarden);

        plantRepository.save(oldestPlant);
        plantRepository.save(middlePlant);
        plantRepository.save(newestPlant);
        PlantService plantService = new PlantService(plantRepository);

        List<Plant> newestPlants = plantService.findNewestPlantsByGardenerId(testGardener.getId());

        Assertions.assertNotNull(newestPlants);
        Assertions.assertEquals(3, newestPlants.size());
        Assertions.assertEquals(newestPlant.getId(), newestPlants.get(0).getId());
        Assertions.assertEquals(middlePlant.getId(), newestPlants.get(1).getId());
        Assertions.assertEquals(oldestPlant.getId(), newestPlants.get(2).getId());
    }

    @Test
    void FindNewestPlantsByGardenerId_WhenOnePlant_ShouldReturnThatPlant() {
        gardenerFormRepository.save(testGardener);

        Garden testGarden = new Garden("Botanical", "20 Marquess street", null, "ChristChurch", "New Zealand", "8870", testGardener, "");
        gardenRepository.save(testGarden);

        Plant plant = new Plant("Plant1", "1", "Description", "2024-01-01", testGarden);

        plantRepository.save(plant);
        PlantService plantService = new PlantService(plantRepository);

        List<Plant> result = plantService.findNewestPlantsByGardenerId(testGardener.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(plant, result.get(0));
    }

    @Test
    void FindNewestPlantsByGardenerId_WhenTwoPlantsFromDifferentGardens_ShouldReturnOnlyOne() {
        Gardener otherGardener = new Gardener("Test2", "Gardener",
                LocalDate.of(2024, 4, 15), "testgardener2@gmail.com",
                "Password2!");

        gardenerFormRepository.save(testGardener);
        gardenerFormRepository.save(otherGardener);

        Garden testGarden = new Garden("Botanical", "20 Marquess street", null, "ChristChurch", "New Zealand", "8870", testGardener, "");
        gardenRepository.save(testGarden);
        Garden otherGarden = new Garden("Floral", "21 Marquess street", null, "Canterbury", "New Zealand", "8870", otherGardener, "");
        gardenRepository.save(otherGarden);

        Plant plant1 = new Plant("Plant1", "1", "Description", "2024-01-01", testGarden);
        Plant plant2 = new Plant("Plant2", "2", "Description", "2024-01-03", otherGarden);
        Plant plant3 = new Plant("Plant3", "3", "Description", "2024-01-03", otherGarden);

        plantRepository.save(plant1);
        plantRepository.save(plant2);
        plantRepository.save(plant3);
        PlantService plantService = new PlantService(plantRepository);

        List<Plant> result = plantService.findNewestPlantsByGardenerId(testGardener.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(plant1, result.get(0));
    }

    @Test
    void FindNewestPlantsByGardenerId_EmptyDatabase_ShouldReturnEmptyList() {
        gardenerFormRepository.save(testGardener);
        Garden testGarden = new Garden("Botanical", "20 Marquess street", null, "ChristChurch", "New Zealand", "8870", testGardener, "");
        gardenRepository.save(testGarden);
        PlantService plantService = new PlantService(plantRepository);

        List<Plant> result = plantService.findNewestPlantsByGardenerId(testGardener.getId());

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }




}
