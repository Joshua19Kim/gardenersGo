package nz.ac.canterbury.seng302.gardenersgrove.integration.service;

import io.cucumber.java.Before;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

@DataJpaTest
@Import(GardenService.class)
public class GardenServiceTest {
    @Autowired
    private GardenRepository gardenRepository;
    @Autowired
    private GardenerFormRepository gardenerFormRepository;
    @Autowired
    private LostPasswordTokenRepository lostPasswordTokenRepository;
    private GardenService gardenService;
    private GardenerFormService gardenerFormService;
    private Gardener testGardener;
    private int totalGardens;

    @BeforeEach
    public void setUp() {
        gardenService = new GardenService(gardenRepository);
        gardenerFormService = new GardenerFormService(gardenerFormRepository, lostPasswordTokenRepository);
        totalGardens = 12;
        testGardener = new Gardener("Test", "Gardener",
                LocalDate.of(2024, 4, 1), "testgardener@gmail.com",
                "Password1!");
        gardenerFormService.addGardener(testGardener);

        for (int i = 0; i < totalGardens; i++) {
            Garden newGarden = new Garden("Botanical",
                    "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", testGardener, "");
            newGarden.setIsGardenPublic(true);
            newGarden.setCreationDate(LocalDate.of(2000, 3, 10 + i));
            gardenService.addGarden(newGarden);
        }
    }

    @AfterEach
    public void tearDown() {
        gardenRepository.deleteAll();
        gardenerFormRepository.deleteAll();
    }

    @Test
    public void GardenAdded_ValidInputs_GardenSavedToRepository() {
        GardenService gardenService = new GardenService(new GardenRepository() {
            @Override
            public List<Garden> findAll(Sort sort) {
                return null;
            }

            @Override
            public Page<Garden> findAll(Pageable pageable) {
                return null;
            }

            @Override
            public void flush() {

            }

            @Override
            public <S extends Garden> S saveAndFlush(S entity) {
                return null;
            }

            @Override
            public <S extends Garden> List<S> saveAllAndFlush(Iterable<S> entities) {
                return null;
            }

            @Override
            public void deleteAllInBatch(Iterable<Garden> entities) {

            }

            @Override
            public void deleteAllByIdInBatch(Iterable<Long> longs) {

            }

            @Override
            public void deleteAllInBatch() {

            }

            @Override
            public Garden getOne(Long aLong) {
                return null;
            }

            @Override
            public Garden getById(Long aLong) {
                return null;
            }

            @Override
            public Garden getReferenceById(Long aLong) {
                return null;
            }

            @Override
            public <S extends Garden> Optional<S> findOne(Example<S> example) {
                return Optional.empty();
            }

            @Override
            public <S extends Garden> List<S> findAll(Example<S> example) {
                return null;
            }

            @Override
            public <S extends Garden> List<S> findAll(Example<S> example, Sort sort) {
                return null;
            }

            @Override
            public <S extends Garden> Page<S> findAll(Example<S> example, Pageable pageable) {
                return null;
            }

            @Override
            public <S extends Garden> long count(Example<S> example) {
                return 0;
            }

            @Override
            public <S extends Garden> boolean exists(Example<S> example) {
                return false;
            }

            @Override
            public <S extends Garden, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
                return null;
            }

            @Override
            public Optional<Garden> findById(long id) {
                return Optional.empty();
            }

            @Override
            public List<Garden> findAll() {
                return null;
            }

            @Override
            public List<Garden> findByGardenerId(Long gardenerId) {
                return List.of();
            }

            @Override
            public <S extends Garden> S save(S entity) {
                Assertions.assertEquals(entity.getName(), "Botanical");
                Assertions.assertEquals(entity.getLocation(), "Homestead Lane");
                Assertions.assertEquals(entity.getSize(), "100");
                return entity;
            }

            @Override
            public <S extends Garden> List<S> saveAll(Iterable<S> entities) {
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
            public List<Garden> findAllById(Iterable<Long> longs) {
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

            @Override
            public void updateLastNotifiedbyId(Long gardenId, LocalDate date){

            }

            @Override
            public Page<Garden> findAllPublicGardens(Pageable pageable) {
                return null;
            }

        });
        gardenService.addGarden(new Garden("Botanical",
                "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", testGardener, "")
        );
    }

    @Test
    public void GardenAdded_ValidInputs_GardenReturned() {
        Garden garden = gardenService.addGarden(new Garden("Botanical",
                "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", testGardener, ""));
        Assertions.assertEquals(garden.getName(), "Botanical");
        Assertions.assertEquals(garden.getLocation(), "Homestead Lane");
        Assertions.assertEquals(garden.getSize(), "100");
    }

    @Test
    public void GardensPaginatedRequested_PageNumberAndSizeGiven_GardensReturned() {
        int pageNo = 0;
        int pageSize = 10;
        Page<Garden> gardensPage = gardenService.getGardensPaginated(pageNo, pageSize);
        Assertions.assertEquals(Math.ceil((double) totalGardens /pageSize), gardensPage.getTotalPages());

        List<Garden> gardensListCopy = new ArrayList<>(gardensPage.getContent());
        gardensListCopy.sort(new Comparator<Garden>() {
            @Override
            public int compare(Garden garden1, Garden garden2) {
                return garden1.getCreationDate().compareTo(garden2.getCreationDate());
            }
        });

        Assertions.assertEquals(gardensPage.getContent(), gardensListCopy.reversed());

    }

    @Test
    public void GardensPaginatedRequested_PageNumberOutOfRange_NoGardensReturned() {
        int pageNo = 3;
        int pageSize = 10;
        Page<Garden> gardensPage = gardenService.getGardensPaginated(pageNo, pageSize);
        Assertions.assertEquals(Math.ceil((double) totalGardens /pageSize), gardensPage.getTotalPages());
        Assertions.assertTrue(gardensPage.getContent().isEmpty());
    }

    @Test
    public void GardensPaginatedRequested_OneGardenPerPage_GardensReturned() {
        int pageNo = 1;
        int pageSize = 1;
        Page<Garden> gardensPage = gardenService.getGardensPaginated(pageNo, pageSize);
        Assertions.assertEquals(totalGardens, gardensPage.getTotalPages());
        Assertions.assertEquals(pageSize, gardensPage.getContent().size());
    }

    @Test
    public void GardenVisitAdded_ValidInputs_GardenVisitReturned() {
        Garden garden = gardenService.addGarden(new Garden("Botanical",
                "Homestead Lane", null, "Christchurch", "New Zealand", null, "100", testGardener, ""));
        Assertions.assertEquals(garden.getName(), "Botanical");
        Assertions.assertEquals(garden.getLocation(), "Homestead Lane");
        Assertions.assertEquals(garden.getSize(), "100");
    }
}
