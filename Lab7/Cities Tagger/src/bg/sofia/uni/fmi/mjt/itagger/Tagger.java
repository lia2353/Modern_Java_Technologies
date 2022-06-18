package bg.sofia.uni.fmi.mjt.itagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tagger {
    public Map<String, String> cityCountry;
    private Map<String, Integer> taggedCities;
    private long taggedCitiesCount;

    /**
     * Creates a new instance of Tagger for a given list of city/country pairs
     *
     * @param citiesReader a java.io.Reader input stream containing list of cities and countries
     *                     in the specified CSV format
     */
    public Tagger(Reader citiesReader) throws IOException {
        cityCountry = new HashMap<>();
        taggedCities = new HashMap<>();
        loadCityCountry(citiesReader);
    }

    private void loadCityCountry(Reader citiesReader) throws IOException {
        BufferedReader br = new BufferedReader(citiesReader);
        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split(",");
            String cityNameLowerCase = tokens[0].toLowerCase();
            String cityNameLowerCaseCapitalFirst = cityNameLowerCase.substring(0, 1).toUpperCase()
                    + cityNameLowerCase.substring(1).toLowerCase();
            cityCountry.put(cityNameLowerCaseCapitalFirst, tokens[1]);
        }
    }

    /**
     * Processes an input stream of a text file, tags any cities and outputs result
     * to a text output stream.
     *
     * @param text   a java.io.Reader input stream containing text to be processed
     * @param output a java.io.Writer output stream containing the result of tagging
     */
    public void tagCities(Reader text, Writer output) throws IOException {
        taggedCities.clear();
        taggedCitiesCount = 0;

        BufferedReader bufferedReader = new BufferedReader(text);
        BufferedWriter bufferedWriter = new BufferedWriter(output);

        String inputLine;
        boolean isFirstLine = true;
        while ((inputLine = bufferedReader.readLine()) != null) {
            if(!isFirstLine) {
                bufferedWriter.newLine();
            }
            String[] words = inputLine.split("\\s+");
            String outputLine = inputLine;
            for(String word : words) {
                String strippedWord = word.replaceAll("[^a-zA-Z ]+", "")
                                         .replaceAll("\\s+", "")
                                         .trim();
                if(!strippedWord.isEmpty()) {
                    String cityNameLowerCase = strippedWord.toLowerCase();
                    String cityNameLowerCaseCapitalFirst = cityNameLowerCase.substring(0, 1).toUpperCase()
                            + cityNameLowerCase.substring(1).toLowerCase();
                    if(cityCountry.containsKey(cityNameLowerCaseCapitalFirst)) {
                        String country = cityCountry.get(cityNameLowerCaseCapitalFirst);
                        String replacement = String.format("<city country=\"%s\">%s</city>", country, strippedWord);
                        outputLine = outputLine.replaceAll(strippedWord, replacement);
                        Integer taggedCount = taggedCities.get(cityNameLowerCaseCapitalFirst);
                        taggedCount = taggedCount != null ? taggedCount + 1 : 1;
                        taggedCities.put(cityNameLowerCaseCapitalFirst, taggedCount);
                        taggedCitiesCount++;
                    }
                }
            }
            bufferedWriter.write(outputLine);
            isFirstLine = false;
            bufferedWriter.flush();
        }
    }

    /**
     * Returns a collection the top @n most tagged cities' unique names
     * from the last tagCities() invocation. Note that if a particular city has been tagged
     * more than once in the text, just one occurrence of its name should appear in the result.
     * If @n exceeds the total number of cities tagged, return as many as available
     * If tagCities() has not been invoked at all, return an empty collection.
     *
     * @param n the maximum number of top tagged cities to return
     * @return a collection the top @n most tagged cities' unique names
     * from the last tagCities() invocation.
     */
    public Collection<String> getNMostTaggedCities(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("N cannot be negative number");
        }

        List<Map.Entry<String, Integer>> sortedCities = new ArrayList<>(taggedCities.entrySet());
        sortedCities.sort(new MapComparator());

        List<String> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(sortedCities.get(i).getKey());
        }
        return result;
    }
    private class MapComparator implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
            Integer firstValue = taggedCities.get(e1.getKey());
            firstValue = firstValue != null ? firstValue : 0;

            Integer secondValue = taggedCities.get(e2.getKey());
            secondValue = secondValue != null ? secondValue : 0;

            return Integer.compare(secondValue, firstValue);
        }
    }

    /**
     * Returns a collection of all tagged cities' unique names
     * from the last tagCities() invocation. Note that if a particular city has been tagged
     * more than once in the text, just one occurrence of its name should appear in the result.
     * If tagCities() has not been invoked at all, return an empty collection.
     *
     * @return a collection of all tagged cities' unique names
     * from the last tagCities() invocation.
     */
    public Collection<String> getAllTaggedCities() {
        return taggedCities.keySet();
    }

    /**
     * Returns the total number of tagged cities in the input text
     * from the last tagCities() invocation
     * In case a particular city has been taged in several occurances, all must be counted.
     * If tagCities() has not been invoked at all, return 0.
     *
     * @return the total number of tagged cities in the input text
     */
    public long getAllTagsCount() {
        return taggedCitiesCount;
    }
}
