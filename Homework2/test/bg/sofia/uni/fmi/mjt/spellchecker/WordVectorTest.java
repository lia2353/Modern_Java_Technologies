package bg.sofia.uni.fmi.mjt.spellchecker;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class WordVectorTest {
    private static WordVector vector;
    private static final double DELTA = 0.01;

    @Test
    public void testGetTwoGramsEmptyWord() {
        String word = "";
        vector = new WordVector(word);
        Map<String, Integer> output = vector.getTwoGrams(word);

        assertTrue(output.isEmpty());
    }

    @Test
    public void testGetTwoGramsDifferentGrams() {
        String word = "hello";
        Map<String, Integer> expected = Map.of("he", 1, "el", 1, "ll", 1, "lo", 1);

        vector = new WordVector(word);
        Map<String, Integer> output = vector.getTwoGrams(word);

        assertTrue(output.keySet().containsAll(expected.keySet()));
        assertTrue(output.values().containsAll(expected.values()));
        assertTrue(expected.keySet().containsAll(output.keySet()));
        assertTrue(expected.values().containsAll(output.values()));
    }

    @Test
    public void testGetTwoGramsDuplicateGrams() {
        String word = "banana";
        Map<String, Integer> expected = Map.of("ba", 1, "an", 2, "na", 2);

        vector = new WordVector(word);
        Map<String, Integer> output = vector.getTwoGrams(word);

        assertTrue(output.keySet().containsAll(expected.keySet()));
        assertTrue(output.values().containsAll(expected.values()));
        assertTrue(expected.keySet().containsAll(output.keySet()));
        assertTrue(expected.values().containsAll(output.values()));
    }

    @Test
    public void testVectorLengthEmptyWord() {
        String word = "";
        vector = new WordVector(word);
        double output = vector.vectorLength();

        assertEquals(0, output, DELTA);
    }

    @Test
    public void testVectorLengthHello() {
        String word = "hello";
        vector = new WordVector(word);
        double output = vector.vectorLength();

        assertEquals(2, output, DELTA);
    }

    @Test
    public void testVectorLengthBanana() {
        String word = "banana";
        vector = new WordVector(word);
        double output = vector.vectorLength();

        assertEquals(3, output, DELTA);
    }

    @Test
    public void testDotProductEmptyWords() {
        String word = "";
        vector = new WordVector(word);
        WordVector otherVector = new WordVector(word);
        double output = vector.dotProduct(otherVector);

        assertEquals(0, output, DELTA);
    }

    @Test
    public void testDotProductNoSameGrams() {
        String word = "hello";
        String otherWord = "banana";
        vector = new WordVector(word);
        WordVector otherVector = new WordVector(otherWord);

        double output = vector.dotProduct(otherVector);

        assertEquals(0, output, DELTA);
    }

    @Test
    public void testDotProductSameWord() {
        String word = "hello";
        vector = new WordVector(word);
        WordVector otherVector = new WordVector(word);

        double output = vector.dotProduct(otherVector);

        assertEquals(4, output, DELTA);
    }

    @Test
    public void testDotProductSomeSameGrams() {
        String word = "hello";
        String otherWord = "shell";
        vector = new WordVector(word);
        WordVector otherVector = new WordVector(otherWord);

        double output = vector.dotProduct(otherVector);

        assertEquals(3, output, DELTA);
    }

    @Test
    public void testCosineSimilarityEmptyWords() {
        String word = "";
        vector = new WordVector(word);
        WordVector otherVector = new WordVector(word);
        double output = vector.cosineSimilarity(otherVector);

        assertEquals(0, output, DELTA);
    }

    @Test
    public void testCosineSimilarityNoSameGrams() {
        String word = "hello";
        String otherWord = "banana";
        vector = new WordVector(word);
        WordVector otherVector = new WordVector(otherWord);

        double output = vector.cosineSimilarity(otherVector);

        assertEquals(0, output, DELTA);
    }

    @Test
    public void testCosineSimilaritySameWord() {
        String word = "hello";
        vector = new WordVector(word);
        WordVector otherVector = new WordVector(word);

        double output = vector.cosineSimilarity(otherVector);

        assertEquals(1, output, DELTA);
    }

    @Test
    public void testCosineSimilaritySomeSameGrams() {
        String word = "hello";
        String otherWord = "shell";
        vector = new WordVector(word);
        WordVector otherVector = new WordVector(otherWord);

        double output = vector.cosineSimilarity(otherVector);

        assertEquals(0.75, output, DELTA);
    }
}
