package nz.ac.canterbury.seng302.gardenersgrove.unit.util;

import nz.ac.canterbury.seng302.gardenersgrove.util.WordFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WordFilterTest {
    @Test
    public void testEmptyInput() {
        assertFalse(WordFilter.doesContainBadWords(""));
    }

    @Test
    public void testNullInput() {
        assertFalse(WordFilter.doesContainBadWords(null));
    }

    @Test
    public void testUppercaseInput() {
        assertTrue(WordFilter.doesContainBadWords("SHIT"));
    }

    @Test
    public void testLowercaseInput() {
        assertTrue(WordFilter.doesContainBadWords("shit"));
    }

    @Test
    public void testMixedCaseInput() {
        assertTrue(WordFilter.doesContainBadWords("ShiT"));
    }

    @Test
    public void testSentenceWithBadWord() {
        assertTrue(WordFilter.doesContainBadWords("shit a bad word."));
    }

    @Test
    public void testSentenceWithoutBadWords() {
        assertFalse(WordFilter.doesContainBadWords("This sentence is fine."));
    }

    @Test
    public void testWordsAreIgnored() {
        assertFalse(WordFilter.doesContainBadWords("Classic and massive."));
    }

    @Test
    public void testLeetspeakInput() {
        assertTrue(WordFilter.doesContainBadWords("sh1t"));
    }
}