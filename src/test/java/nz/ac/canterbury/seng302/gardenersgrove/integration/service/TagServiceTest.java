package nz.ac.canterbury.seng302.gardenersgrove.integration.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TagServiceTest {

    @InjectMocks
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;

    private static Gardener gardener;

    private static List<Garden> gardens;

    @BeforeAll
    public static void setUpClass() {
        gardener = new Gardener("Michael", "Scott", LocalDate.of(1980, 1, 1),
                "dunderMifflin@gmail.com", "password");
        gardens = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            gardens.add(new Garden("My Garden " + i, "Auckland", gardener));
        }
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tagService = new TagService(tagRepository);
    }

    @Test
    public void GetUniqueTags_TagsExist_UniqueTagsReturned() {
        Long myGardenId = 1L;

        List<Tag> allTags = new ArrayList<>();
        allTags.add(new Tag("berries", gardens.get(0)));
        allTags.add(new Tag("fruit", gardens.get(0)));
        allTags.add(new Tag("veges", gardens.get(1)));
        allTags.add(new Tag("flowers", gardens.get(1)));
        allTags.add(new Tag("herbs", gardens.get(2)));
        allTags.add(new Tag("berries", gardens.get(2)));

        List<String> myTagNames = List.of("veges", "flowers");

        Mockito.when(tagService.getAllTags()).thenReturn(allTags);
        Mockito.when(tagService.getTags(myGardenId)).thenReturn(myTagNames);

        Set<String> expectedUniqueNames = Set.of("berries", "fruit", "herbs");
        Set<String> actualUniqueNames = tagService.getUniqueTagNames(myGardenId);

        Assertions.assertEquals(expectedUniqueNames, actualUniqueNames);

    }
}
