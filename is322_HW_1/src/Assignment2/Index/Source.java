package Assignment2.Index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Source {
    // document id
    private Integer Id;
    // docuemnt path
    private String FilePath;
    // word frequency
    private HashMap<String, Integer> WordsFreq = new HashMap<>();

    // intialinzing the source
    public Source(Integer id, String filePath) {
        this.Id = id;
        this.FilePath = filePath;
        // traversing the document
        try (BufferedReader file = new BufferedReader(new FileReader(filePath))) {
            String ln;
            while ((ln = file.readLine()) != null) {
                // splites the docuemnt by words
                String[] words = ln.split("\\W+");
                for (String word : words) {
                    // makes sure that the word is not empty
                    if (word.isEmpty())
                        continue;
                    word = word.toLowerCase();
                    WordsFreq.put(word, WordsFreq.getOrDefault(word, 0) + 1);
                }
            }
        } catch (IOException e) {
            System.out.println("File " + filePath + " not found. Skip it");
        }
    }

    // returns the term frequency inside the document
    public double term_freq(String Term) {
        int frequency = freq(Term);
        if (frequency == 0)
            return 0;
        double ret = 1 + Math.log10(frequency);
        return ret;
    }

    // return the frequenct
    public int freq(String Term) {
        if (WordsFreq.containsKey(Term))
            return WordsFreq.get(Term);
        return 0;
    }

    // return the total number of words inside the document
    public int word_count() {
        int sum = 0;
        for (int freq : this.WordsFreq.values()) {
            sum += freq;
        }
        return sum;
    }
}
