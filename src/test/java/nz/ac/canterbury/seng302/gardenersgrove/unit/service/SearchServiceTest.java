package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
public class SearchServiceTest {

    @Autowired
    private GardenerFormRepository gardenerFormRepository;

    private SearchService searchService;

    @BeforeEach
    public void setUp() {
        searchService = new SearchService(gardenerFormRepository);
        IntStream.range(0, 10).forEach(i -> {
            Gardener gardener = new Gardener(String.valueOf(i), "test", null, "test" + i, "test");
            gardenerFormRepository.save(gardener);
        });

        Gardener gardener = new Gardener("1", "test", null, "test", "test");
        gardenerFormRepository.save(gardener);

    }

    @Test
    void searchGardenerByEmail() {
        IntStream.range(0, 10).forEach(i -> {
            Gardener gardener = searchService.searchGardenersByEmail("test" + i).get();
            assertEquals("test" + i, gardener.getEmail());
        });
    }

    @Test
    void searchGardenersByFullName() {
        List<Gardener> gardenerList = searchService.searchGardenersByFullName(1 + " test");
        assertEquals(2, gardenerList.size());
    }
}
