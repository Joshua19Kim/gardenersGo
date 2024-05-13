package nz.ac.canterbury.seng302.gardenersgrove.unit.util;

import nz.ac.canterbury.seng302.gardenersgrove.util.WordFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class WordFilterTest {
    @Test
    public void ContainsBadWord_AlphabeticLowercase_ReturnsTrue() {
        String input = "ass";
        Assertions.assertTrue(WordFilter.doesContainBadWords(input));
    }
}