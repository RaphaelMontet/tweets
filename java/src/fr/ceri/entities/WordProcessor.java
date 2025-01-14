package fr.ceri.entities;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.frenchStemmer;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class WordProcessor
{
    private Map<String, String> wordList;
    private String targetDataFile;
    private String inputFile;

    public WordProcessor()
    {
        wordList = new HashMap<>();
        inputFile = "../common/data/external/FEEL.csv";
        targetDataFile = "../common/data/annotated/words_full.json";
    }

    public Map<String, String> getWordList()
    {
        return wordList;
    }

    public String getTargetDataFile()
    {
        return targetDataFile;
    }

    public void convertToWordMapAndFormat() throws IOException
    {
        Reader input = Files.newBufferedReader(Paths.get(inputFile));
        CSVFormat format = CSVFormat.DEFAULT.withDelimiter(';');
        CSVParser parser = new CSVParser(input, format);

        List<CSVRecord> records = parser.getRecords();

        for (CSVRecord record : records)
        {
            String word = record.get(1);

            // On traite les entrées ne contenant qu'un mot
            if (!word.contains(" "))
            {
                word = word.toLowerCase();

                // On ne conserve que le premier mot dans les entrées polymorphiques (ex: planteur|plante → planteur)
                if (word.contains("|"))
                {
                    List<String> words = Arrays.asList(word.split("\\|"));
                    word = words.get(0).length() < words.get(1).length() ? words.get(0) : words.get(1);
                }

                // Stemming (commenté pour version sans stemming)
                // SnowballStemmer stemmer = new frenchStemmer();
                // stemmer.setCurrent(word);
                // stemmer.stem();
                // wordList.put(stemmer.getCurrent(), record.get(2));

                wordList.put(word, record.get(2));
            }
        }
    }
}
