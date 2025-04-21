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
    private Integer Id;
    private String FilePath;

    private HashMap<String, Integer> WordsFreq = new HashMap<>();

    public Source(Integer id, String filePath) {
        this.Id = id;
        this.FilePath = filePath;
        try (BufferedReader file = new BufferedReader(new FileReader(filePath))) {
            String ln;
            while ((ln = file.readLine()) != null) {

                String[] words = ln.split("\\W+");
                for (String word : words) {
                    word = word.toLowerCase();
                    if (this.WordsFreq.containsKey(word)) {
                        this.WordsFreq.put(word, this.WordsFreq.get(word) + 1);
                    } else {
                        this.WordsFreq.put(word, 1);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("File " + filePath + " not found. Skip it");
        }
    }

    public double term_freq(String Term) {
        int frequency = freq(Term);
        if (frequency == 0)
            return 0;
        double ret = 1 + Math.log10(frequency);
        return ret;
    }

    public int freq(String Term) {
        if (WordsFreq.containsKey(Term))
            return WordsFreq.get(Term);
        return 0;
    }

    public int word_count() {
        int sum = 0;
        for (int freq : this.WordsFreq.values()) {
            sum += freq;
        }
        return sum;
    }
}
