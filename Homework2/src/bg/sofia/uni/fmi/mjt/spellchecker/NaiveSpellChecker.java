package bg.sofia.uni.fmi.mjt.spellchecker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NaiveSpellChecker implements SpellChecker {
    private static final String NON_ALPHANUMERIC_START_AND_END_REGEX = "^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+$";

    private static Set<String> stopwords;
    private static Map<String, WordVector> dictionary; // (word, wordVector)
    private Map<Integer, List<String>> mistakenWordsByLine;
    private int lineNumber;

    /**
     * Creates a new instance of NaiveSpellCheckTool, based on a dictionary of words and stop words
     *
     * @param dictionaryReader a java.io.Reader input stream containing list of words which will serve as a
     *                         dictionary for the tool
     * @param stopwordsReader  a java.io.Reader input stream containing list of stopwords
     */
    public NaiveSpellChecker(Reader dictionaryReader, Reader stopwordsReader) {
        stopwords = new HashSet<>();
        loadStopwords(stopwordsReader);
        dictionary = new HashMap<>();
        loadDictionary(dictionaryReader);

        mistakenWordsByLine = new HashMap<>();
    }

    private void loadStopwords(Reader inputReader) {
        var reader = new BufferedReader(inputReader);
        stopwords = reader.lines()
                .map(String::toLowerCase)
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    private void loadDictionary(Reader inputReader) {
        var reader = new BufferedReader(inputReader);
        dictionary = reader.lines()
                .map(String::toLowerCase)
                .map(s -> s.replaceAll(NON_ALPHANUMERIC_START_AND_END_REGEX, ""))
                .filter(w -> w.length() > 1)
                .distinct()
                .collect(Collectors.toMap(w -> w, w -> new WordVector(w)));
    }

    @Override
    public void analyze(Reader textReader, Writer output, int suggestionsCount) {
        if (textReader == null || output == null) {
            throw new IllegalArgumentException("Provided arguments cannot be null.");
        }
        if (suggestionsCount < 0) {
            throw new IllegalArgumentException("Provided number argument cannot be negative number.");
        }

        BufferedReader bufferedReader = new BufferedReader(textReader);
        BufferedWriter bufferedWriter = new BufferedWriter(output);

        Metadata textMetadata = new Metadata(0, 0, 0);
        mistakenWordsByLine.clear();
        lineNumber = 1;
        try {
            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                bufferedWriter.write(inputLine + "\n");
                bufferedWriter.flush();

                Reader reader = new StringReader(inputLine);
                textMetadata = textMetadata.combine(metadata(reader));
                lineNumber++;
            }
            writeMetadataInformation(textMetadata, bufferedWriter);
            writeMistakesInformation(suggestionsCount, bufferedWriter);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void writeMetadataInformation(Metadata metadata, BufferedWriter output) throws IOException {
        output.write("= = = Metadata = = =\n");
        output.write(metadata.characters() + " characters, "
                + metadata.words() + " words, "
                + metadata.mistakes() + " spelling issue(s) found\n");
        output.flush();
    }

    private void writeMistakesInformation(int suggestionsCount, BufferedWriter output) throws IOException {
        output.write("= = = Findings = = =\n");
        output.flush();

        if (mistakenWordsByLine.isEmpty()) {
            output.write("No spelling issues found\n");
            output.flush();
            return;
        }

        for (Map.Entry<Integer, List<String>> lineMistakes : mistakenWordsByLine.entrySet()) {
            for (String word : lineMistakes.getValue()) {
                output.write("Line #" + lineMistakes.getKey()
                        + ", {" + word + "} - Possible suggestions are {");
                output.flush();
                writeSuggestionWordsInformation(word, suggestionsCount, output);
                output.write("}\n");
                output.flush();
            }
        }
    }

    private void writeSuggestionWordsInformation(String word, int suggestionsCount, BufferedWriter output)
            throws IOException {
        List<String> suggestionWords = findClosestWords(word, suggestionsCount);
        boolean firstSuggestion = true;
        for (String suggestionWord : suggestionWords) {
            if (firstSuggestion) {
                output.write(suggestionWord);
                firstSuggestion = false;
            } else {
                output.write(", " + suggestionWord);
            }
        }
        output.flush();
    }

    @Override
    public Metadata metadata(Reader textReader) {
        if (textReader == null) {
            throw new IllegalArgumentException("Provided argument cannot be null.");
        }
        int characters = 0;
        int words = 0;
        int mistakes = 0;

        var reader = new BufferedReader(textReader);
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                line = line.toLowerCase();

                characters += Stream.of(line)
                        .map(w -> w.replaceAll("\\s+", ""))
                        .collect(Collectors.joining())
                        .length();

                words += Stream.of(line)
                        .flatMap(l -> Arrays.stream(l.trim().split("\\s")))
                        .map(w -> w.replaceAll(NON_ALPHANUMERIC_START_AND_END_REGEX, ""))
                        .filter(w -> w.length() > 1)
                        .filter(w -> !(stopwords.contains(w)))
                        .mapToInt(w -> 1)
                        .sum();

                mistakes += getMistakenWordsInLine(line, lineNumber);
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }

        return new Metadata(characters, words, mistakes);
    }

    private int getMistakenWordsInLine(String line, int lineNumber) {
        List<String> mistakenWords = new ArrayList<>();

        int mistakesCount = Stream.of(line)
                .flatMap(l -> Arrays.stream(l.trim().split("\\s")))
                .map(w -> w.replaceAll(NON_ALPHANUMERIC_START_AND_END_REGEX, ""))
                .filter(w -> w.length() > 1)
                .filter(w -> !(stopwords.contains(w)))
                .filter(w -> !(dictionary.containsKey(w)))
                .peek(w -> mistakenWords.add(w))
                .mapToInt(w -> 1)
                .sum();

        mistakenWordsByLine.put(lineNumber, mistakenWords);
        return mistakesCount;
    }

    @Override
    public List<String> findClosestWords(String word, int n) {
        if (word == null) {
            throw new IllegalArgumentException("Provided arguments cannot be null.");
        }
        if (n < 0) {
            throw new IllegalArgumentException("Provided number argument cannot be negative number.");
        }

        return dictionary.keySet()
                .stream()
                .sorted((String w1, String w2) -> compareBySimilarityToWord(word, w1, w2))
                .limit(n)
                .collect(Collectors.toList());
    }

    public int compareBySimilarityToWord(String word, String w1, String w2) {
        WordVector vector = new WordVector(word);
        Double similarityRatio1 = vector.cosineSimilarity(dictionary.get(w1));
        Double similarityRatio2 = vector.cosineSimilarity(dictionary.get(w2));
        return similarityRatio2.compareTo(similarityRatio1);
    }

}
