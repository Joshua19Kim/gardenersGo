package nz.ac.canterbury.seng302.gardenersgrove.unit.util;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Garden;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Gardener;
import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;
import nz.ac.canterbury.seng302.gardenersgrove.service.TagService;
import nz.ac.canterbury.seng302.gardenersgrove.util.TagValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TagValidationTest {
  @Mock private TagService tagService;
  private TagValidation tagValidation;
  private final Garden mockGarden =
      new Garden(
          "Garden", "99 test address", "Ilam", "Christchurch", "New Zealand", "8025", new Gardener("Bob", "Joe", null, "a@gmail.com", "Password1!"));
  private final Tag mockTag = new Tag("Valid", mockGarden);

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    tagValidation = new TagValidation(tagService);
  }

  @ParameterizedTest
  @CsvSource(
      value = {
        "ValidTag123: ",
        "ThisTagNameIsWayTooLongAndInvalid: A tag cannot exceed 25 characters",
        "Invalid@Tag: The tag name must only contain alphanumeric characters, spaces, -, _, ', or \"",
        "-: The tag name must contain at least one alphanumeric character"
      },
      delimiter = ':')
  public void testValidateTag(String tagName, String expectedMessage) {
    Optional<String> result = tagValidation.validateTag(tagName);
    if (expectedMessage == null) {
      assertFalse(result.isPresent());
    } else {
      assertTrue(result.isPresent());
      assertEquals(expectedMessage, result.get());
    }
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "ValidTag",
        "AnotherValidTag",
        "YetAnotherValidTag",
        "-a",
        "_a",
        "'a",
        "\"a",
        "9",
        "a ",
        "ö",
        "é",
        "Ā"
      })
  public void testValidateTag_validTags(String validTag) {
    Optional<String> result = tagValidation.validateTag(validTag);
    assertFalse(result.isPresent());
  }

  @Test
  void testCheckTagInUse_tagExists() {
    when(tagService.findTagByNameAndGarden(mockTag.getName(), mockTag.getGarden()))
        .thenReturn(Optional.of(mockTag));
    Optional<String> result = tagValidation.checkTagInUse(mockTag.getName(), mockGarden);
    assertTrue(result.isPresent());
    assertEquals("Used", result.get());
  }

  @Test
  void testCheckTagInUse_tagDoesNotExist() {
    String tagName = "NonExistingTag";
    when(tagService.findTagByNameAndGarden(tagName, mockGarden)).thenReturn(Optional.empty());
    Optional<String> result = tagValidation.checkTagInUse(tagName, mockGarden);
    assertFalse(result.isPresent());
  }
}
