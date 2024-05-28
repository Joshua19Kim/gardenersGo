package nz.ac.canterbury.seng302.gardenersgrove.integration.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Plant;
import nz.ac.canterbury.seng302.gardenersgrove.repository.PlantRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.PlantService;
import org.junit.jupiter.api.Assertions;
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
    Gardener testGardener = new Gardener("Test", "Gardener",
            LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
            "Password1!");

    @Test
    public void PlantAdded_ValidInputs_PlantSavedToRepository() {
        Garden garden = new Garden("Botanical","20 Marquess street",null,"ChristChurch","New Zealand", "8870", testGardener);
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

    @Autowired
    private PlantRepository plantRepository;

    @Test
    public void PlantAdded_ValidInputs_PlantReturned() {
        PlantService plantService = new PlantService(plantRepository);
        Garden garden = new Garden("Botanical","20 Marquess street",null,"ChristChurch","New Zealand", "8870", testGardener);
        Plant plant = plantService.addPlant(new Plant("Flower","2", "Rose", "08/02/2024", garden));
        Assertions.assertEquals(plant.getName(), "Flower");
        Assertions.assertEquals(plant.getCount(), "2");
        Assertions.assertEquals(plant.getDescription(), "Rose");
        Assertions.assertEquals(plant.getDatePlanted(), "08/02/2024");
        Assertions.assertEquals(plant.getGarden(), garden);
    }


}
