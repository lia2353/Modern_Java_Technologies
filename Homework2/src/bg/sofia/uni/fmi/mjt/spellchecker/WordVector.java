package bg.sofia.uni.fmi.mjt.spellchecker;

import java.util.HashMap;
import java.util.Map;

public class WordVector {
    private Map<String, Integer> vector; //(gram, occurrences count)
    private double length;

    public WordVector(String word) {
        vector = getTwoGrams(word);
        length = (word.isBlank()) ? 1 : vectorLength();
    }

    //Two-grams of "banana" - (ba, 1) , (an, 2), (na, 2)
    Map<String, Integer> getTwoGrams(String word) {
        Map<String, Integer> wordTwoGrams = new HashMap<>();

        for (int i = 0; i < word.length() - 1; ++i) { //all two-letter substrings are word.length()-1
            String twoGram = word.substring(i, i + 2);
            int occurrencesCount = (wordTwoGrams.containsKey(twoGram)) ? wordTwoGrams.get(twoGram) + 1 : 1;
            wordTwoGrams.put(twoGram, occurrencesCount);
        }
        return wordTwoGrams;
    }

    //|v| = sqrt(x1^2 + x2^2 + ... + xn^2)
    double vectorLength() {
        return Math.sqrt(
                vector.values()
                        .stream()
                        .map(x -> x * x)
                        .reduce(0, Integer::sum) // why sum() doesn't work here
        );
    }

    //a · b = SUM(ai * bi) i = 1..n
    double dotProduct(WordVector otherVector) {
        double dotProduct = 0;
        for (String gram : otherVector.vector.keySet()) {
            if (this.vector.containsKey(gram)) {
                dotProduct += (this.vector.get(gram) * otherVector.vector.get(gram));
            }
        }
        return dotProduct;
    }

    //V1 . V2 / (|V1| * |V2|)
    public double cosineSimilarity(WordVector otherVector) {
        return dotProduct(otherVector) / (this.length * otherVector.length);
    }
}
