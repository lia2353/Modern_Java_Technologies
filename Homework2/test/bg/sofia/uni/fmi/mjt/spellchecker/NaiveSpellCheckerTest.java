package bg.sofia.uni.fmi.mjt.spellchecker;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NaiveSpellCheckerTest {
    private static SpellChecker spellChecker;

    private static String SAMPLE_STOPWORDS_INPUT = """
            a
            am
            are
            and
            me
            no
            not
            """;
    private static String SAMPLE_DICTIONARY_INPUT = """
            allocate
            cat
            hell
            hello
            human
            humane
            humanism
            Kitty-cat
            shell
            subhuman
            """;

    @Before
    public void setUp() {
        Reader soptwordsReader = new StringReader(SAMPLE_STOPWORDS_INPUT);
        Reader dictionaryReader = new StringReader(SAMPLE_DICTIONARY_INPUT);
        spellChecker = new NaiveSpellChecker(dictionaryReader, soptwordsReader);
    }

    @Test
    public void analyzeNoText() throws IOException {
        Reader noText = new StringReader("");
        Writer output = new StringWriter();
        String expected = "= = = Metadata = = =\n"
                + "0 characters, 0 words, 0 spelling issue(s) found\n"
                + "= = = Findings = = =\n"
                + "No spelling issues found\n";

        spellChecker.analyze(noText, output, 2);
        assertEquals(expected, output.toString());
    }

    @Test
    public void analyzeSomeText() {
        String inputString = " \tHelllo, I !!! am a \ncatt ,not a human!";
        Reader input = new StringReader(inputString);
        Writer output = new StringWriter();
        String expected = inputString
                + "\n= = = Metadata = = =\n"
                + "29 characters, 3 words, 2 spelling issue(s) found\n"
                + "= = = Findings = = =\n"
                + "Line #1, {helllo} - Possible suggestions are {hello, hell, shell}\n"
                + "Line #2, {catt} - Possible suggestions are {cat, kitty-cat, allocate}\n";


        spellChecker.analyze(input, output, 3);
        assertEquals(expected, output.toString());
    }

    @Test
    public void analyzeTextWithSameMistakenWordTwice() {
        String inputString = " \tHelllo, I !!! am a \ncat ,helllo!";
        Reader input = new StringReader(inputString);
        Writer output = new StringWriter();
        String expected = inputString
                + "\n= = = Metadata = = =\n"
                + "25 characters, 3 words, 2 spelling issue(s) found\n"
                + "= = = Findings = = =\n"
                + "Line #1, {helllo} - Possible suggestions are {hello, hell, shell}\n"
                + "Line #2, {helllo} - Possible suggestions are {hello, hell, shell}\n";


        spellChecker.analyze(input, output, 3);
        assertEquals(expected, output.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeWithNullArgument() {
        spellChecker.analyze(null, null, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAnalyzeWithNegativeSuggestionsCount() {
        spellChecker.analyze(new StringReader("Some Text"), new StringWriter(), -3);
    }

    @Test
    public void testMetadataNoText() {
        Reader noText = new StringReader("");
        Metadata outputMetadata = spellChecker.metadata(noText);

        assertEquals(0, outputMetadata.characters());
        assertEquals(0, outputMetadata.words());
        assertEquals(0, outputMetadata.mistakes());
    }

    @Test
    public void testMetadataSentenceWithNoMistakes() {
        Reader input = new StringReader("Hello, I am a cat!");
        Metadata outputMetadata = spellChecker.metadata(input);

        assertEquals(14, outputMetadata.characters()); ////'Hello,Iamacat!'.size()
        assertEquals(2, outputMetadata.words()); //'hello' , 'cat'
        assertEquals(0, outputMetadata.mistakes());
    }

    @Test
    public void testMetadataSentenceWithMistakes() {
        Reader input = new StringReader("Hello, I am a bird!");
        Metadata outputMetadata = spellChecker.metadata(input);

        assertEquals(15, outputMetadata.characters()); //'Hello,Iamabird!'.size()
        assertEquals(2, outputMetadata.words()); //'hello' , 'bird'
        assertEquals(1, outputMetadata.mistakes()); //'bird' not in the dictionary
    }

    @Test
    public void testMetadataSentenceWithManyWhitespaces() {
        Reader input = new StringReader("   \tHello, I !!! am a \n\nbird! ");
        Metadata outputMetadata = spellChecker.metadata(input);

        assertEquals(18, outputMetadata.characters()); ////'Hello,I!!!amabird!'.size()
        assertEquals(2, outputMetadata.words()); //'hello' , 'bird'
        assertEquals(1, outputMetadata.mistakes()); //'bird' not in the dictionary
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMetadataWithNullArgument() {
        spellChecker.metadata(null);
    }

    @Test
    public void testFindClosestWordsWordInDictionary() {
        List<String> expected = List.of("human", "humane", "humanism", "subhuman");
        List<String> output = spellChecker.findClosestWords("human", expected.size());

        assertEquals(expected.size(), output.size());
        assertTrue(output.containsAll(expected));
        assertTrue(expected.containsAll(output));
    }

    @Test
    public void testFindClosestWordsWordNotInDictionaryReturnAllWords() {
        List<String> expected = List.of(SAMPLE_DICTIONARY_INPUT.toLowerCase().split("\\s+"));
        List<String> output = spellChecker.findClosestWords("someWordNotInDictionary", expected.size() + 2);

        assertEquals(expected.size(), output.size());
        assertTrue(output.containsAll(expected));
        assertTrue(expected.containsAll(output));
    }

    @Test
    public void testFindClosestWordsFindTheExactWord() {
        List<String> expected = List.of("hello");
        List<String> output = spellChecker.findClosestWords("hello", expected.size());

        assertEquals(expected.size(), output.size());
        assertTrue(output.containsAll(expected));
        assertTrue(expected.containsAll(output));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindClosestWordsWithNullArgument() {
        spellChecker.findClosestWords(null, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindClosestWordsWithNegativeSuggestionsCount() {
        spellChecker.findClosestWords("word", -3);
    }

}
