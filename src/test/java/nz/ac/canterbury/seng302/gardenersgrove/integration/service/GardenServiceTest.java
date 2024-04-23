package nz.ac.canterbury.seng302.gardenersgrove.integration.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import(GardenService.class)
public class GardenServiceTest {

    @Test
    public void GardenAdded_ValidInputs_GardenSavedToRepository() {
        GardenService gardenService = new GardenService(new GardenRepository() {
            @Override
            public Optional<Garden> findById(long id) {
                return Optional.empty();
            }

            @Override
            public List<Garden> findAll() {
                return null;
            }

            @Override
            public <S extends Garden> S save(S entity) {
                Assertions.assertEquals(entity.getName(), "Botanical");
                Assertions.assertEquals(entity.getLocation(), "Homestead Lane");
                Assertions.assertEquals(entity.getSize(), "100");
                return entity;
            }

            @Override
            public <S extends Garden> Iterable<S> saveAll(Iterable<S> entities) {
                return null;
            }

            @Override
            public Optional<Garden> findById(Long aLong) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(Long aLong) {
                return false;
            }

            @Override
            public Iterable<Garden> findAllById(Iterable<Long> longs) {
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
            public void delete(Garden entity) {

            }

            @Override
            public void deleteAllById(Iterable<? extends Long> longs) {

            }

            @Override
            public void deleteAll(Iterable<? extends Garden> entities) {

            }

            @Override
            public void deleteAll() {

            }
        });
        gardenService.addGarden(new Garden("Botanical","Homestead Lane", "100"));
    }

    @Autowired
    private GardenRepository gardenRepository;

    @Test
    public void GardenAdded_ValidInputs_GardenReturned() {
        GardenService gardenService = new GardenService(gardenRepository);
        Garden garden = gardenService.addGarden(new Garden("Botanical","Homestead Lane", "100"));
        Assertions.assertEquals(garden.getName(), "Botanical");
        Assertions.assertEquals(garden.getLocation(), "Homestead Lane");
        Assertions.assertEquals(garden.getSize(), "100");
    }


}
