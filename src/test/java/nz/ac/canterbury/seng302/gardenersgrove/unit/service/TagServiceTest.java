package nz.ac.canterbury.seng302.gardenersgrove.unit.service;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.repository.TagRepository;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import nz.ac.canterbury.seng302.gardenersgrove.util.WriteEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TagServiceTest {



    private TagService tagService;

    private TagRepository tagRepository;

    private WriteEmail writeEmail;

    private Garden garden;

    private final Long gardenId = 1L;

    @BeforeEach
    public void setUp() {
        tagRepository = Mockito.mock(TagRepository.class);
        tagService = new TagService(tagRepository, writeEmail);
        garden = Mockito.mock(Garden.class);
        garden.setId(gardenId);
    }


    @Test
    public void getAllTags_ReturnsAllTags() {
        List<Tag> expectedTags = Arrays.asList(
                new Tag("Vegetable", garden),
                new Tag("Fruit", garden)
        );
        when(tagRepository.findAll()).thenReturn(expectedTags);

        List<Tag> actualTags = tagService.getAllTags();

        assertEquals(expectedTags, actualTags);
    }

    @Test
    public void addTag_ReturnsAddedTag() {
        Tag tagToAdd = new Tag("NewTag", garden);
        when(tagRepository.save(tagToAdd)).thenReturn(tagToAdd);

        Tag addedTag = tagService.addTag(tagToAdd);

        assertEquals(tagToAdd, addedTag);
    }
    @Test
    public void getTag_WithExistingId_ReturnsTag() {
        Long tagId = 1L;
        Tag expectedTag = new Tag("ExistingTag", garden);
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(expectedTag));

        Optional<Tag> actualTag = tagService.getTag(tagId);

        assertEquals(Optional.of(expectedTag), actualTag);
    }

    @Test
    public void getTag_WithNonExistingId_ReturnsEmptyOptional() {
        Long tagId = 999L;
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        Optional<Tag> actualTag = tagService.getTag(tagId);

        assertEquals(Optional.empty(), actualTag);
    }


    @Test
    public void getUniqueTagNames_WithGardenId_ReturnUniqueTags() {


        List<Tag> allTags = Arrays.asList(
                new Tag("Vegetable", garden),
                new Tag("Fruit", garden),
                new Tag("Herb", garden),
                new Tag("Flower", garden)
        );

        List<String> gardenTags = Arrays.asList("Vegetable", "Fruit");

        when(tagRepository.findAll()).thenReturn(allTags);
        when(tagRepository.getTagsByGardenId(gardenId)).thenReturn(gardenTags);

        Set<String> uniqueTags = tagService.getUniqueTagNames(gardenId);

        Set<String> expectedUniqueTags = new HashSet<>(Arrays.asList("Herb", "Flower"));
        assertEquals(expectedUniqueTags, uniqueTags);

    }


}
