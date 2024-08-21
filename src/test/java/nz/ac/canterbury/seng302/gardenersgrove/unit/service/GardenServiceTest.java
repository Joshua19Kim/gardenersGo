package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import jakarta.transaction.Transactional;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.GardenerFormRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.LostPasswordTokenRepository;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenService;
import nz.ac.canterbury.seng302.gardenersgrove.service.GardenerFormService;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
public class GardenServiceTest {

    @Autowired
    private GardenRepository gardenRepository;
    @Autowired
    private GardenerFormRepository gardenerFormRepository;
    @Autowired
    private LostPasswordTokenRepository lostPasswordTokenRepository;
    @Autowired
    TagRepository tagRepository;
    @MockBean
    WriteEmail writeEmail;
    private GardenService gardenService;
    private final Gardener testGardener = new Gardener("test", "test", LocalDate.of(2010, 10, 10), "email@doe.com", "Password1!");


    @BeforeEach
    public void setUp() {
        gardenService = new GardenService(gardenRepository);
        GardenerFormService gardenerFormService = new GardenerFormService(gardenerFormRepository, lostPasswordTokenRepository);

        gardenerFormService.addGardener(testGardener);
        Garden garden = new Garden("Test garden", "test location", "test suburb", "test city", "test country", "2025", testGardener, "test desc");
        garden.setIsGardenPublic(true);
        gardenService.addGarden(garden);
    }


    @Test
    void SearchGardensByName_OneMatchingGardenReturned() {
        Page<Garden> page = gardenService.getSearchResultsPaginated(0, 10,"Test garden", null, 0L);
        List<Garden> gardens = page.getContent();
        assertEquals("Test garden", gardens.getFirst().getName());
    }

    @Test
    void SearchGardensByEmoji_NoMatchingGardenReturned() {
        // searching for 😂
        Page<Garden> page = gardenService.getSearchResultsPaginated(0, 10,"\uD83D\uDE02", null, 0L);
        List<Garden> gardens = page.getContent();
        assertEquals(0, gardens.size());
    }

    @ParameterizedTest
    @CsvSource(
            value = {
                    "\uD83D\uDE04", // Smile 😄
                    "\uD83E\uDDEC️", // Live 🧬️
                    "\uD83D\uDE02", // Laugh 😂
                    "❤️️" // Love ❤️️
            })
    void AddingGardensWithEmojiInName_SearchGardensByEmoji_MatchingGardenReturned(String emoji) {
        Garden emojiGarden = new Garden(emoji, "test location", "test suburb", "test city", "test country", "2025", testGardener, "test desc");
        emojiGarden.setIsGardenPublic(true);
        gardenService.addGarden(emojiGarden);

        Page<Garden> page = gardenService.getSearchResultsPaginated(0, 10, emoji, null, 0L);
        List<Garden> gardens = page.getContent();
        assertEquals(emojiGarden, gardens.getFirst());
    }

    @ParameterizedTest
    @CsvSource(
            value = {
                    "\uD83D\uDE04", // Smile 😄
                    "\uD83E\uDDEC️", // Live 🧬️
                    "\uD83D\uDE02", // Laugh 😂
                    "❤️️" // Love ❤️️
            })
    void AddingGardensWithEmojiInTag_SearchGardensByEmoji_MatchingGardenReturned(String emoji) {
        Garden emojiGarden = new Garden("test garden", "test location", "test suburb", "test city", "test country", "2025", testGardener, "test desc");
        emojiGarden.setIsGardenPublic(true);
        gardenService.addGarden(emojiGarden);

        Tag tag = new Tag(emoji, emojiGarden);
        TagService tagService = new TagService(tagRepository, writeEmail);
        tagService.addTag(tag);

        Page<Garden> page = gardenService.getSearchResultsPaginated(0, 10, null, Collections.singletonList(emoji), 1L);
        List<Garden> gardens = page.getContent();
        assertEquals(emojiGarden, gardens.getFirst());
    }

    @Test
    void AddingMultipleGardensWithEmojiInTag_SearchGardensByEmoji_AllMatchingGardenReturned() {
        String[] emojis = new String[]{
                "\uD83D\uDE04", // Smile 😄
                "\uD83E\uDDEC️", // Live 🧬️
                "\uD83D\uDE02", // Laugh 😂
                "❤️️" // Love ❤️️
        };

        TagService tagService = new TagService(tagRepository, writeEmail);

        for (String emoji: emojis) {
            Garden emojiGarden = new Garden("test garden", "test location", "test suburb", "test city", "test country", "2025", testGardener, "test desc");
            emojiGarden.setIsGardenPublic(true);
            gardenService.addGarden(emojiGarden);

            Tag tag = new Tag(emoji, emojiGarden);
            tagService.addTag(tag);
        }

        Page<Garden> page = gardenService.getSearchResultsPaginated(0, 10, null, List.of(emojis), 4L);
        List<Garden> gardens = page.getContent();
        assertEquals(4, gardens.size());
    }


}
