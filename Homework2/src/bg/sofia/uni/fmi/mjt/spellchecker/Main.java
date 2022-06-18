package bg.sofia.uni.fmi.mjt.spellchecker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

public class Main {
    public static void main(String[] args) {
        try (var reader1 = new BufferedReader(new FileReader("./resources/stopwords.txt"));
             var reader2 = new BufferedReader(new FileReader("./resources/dictionary.txt"))) {
            NaiveSpellChecker ns = new NaiveSpellChecker(reader2, reader1);
            //System.out.println(ns.findClosestWords("hello", 10));
            Reader catTextReader = new StringReader("hello, I !!! \tam a \ncat ,not a humann!");
            Writer output = new FileWriter("output.txt");
            //Metadata metadata = ns.metadata(catTextReader);
            ns.analyze(catTextReader, output, 2);

        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from the file", e);
        }
    }

}
