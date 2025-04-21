package Assignment2.Index;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvertedIndex {
    private int N = 0;
    private HashMap<Integer, Source> Sources = new HashMap<>();
    private HashMap<String, List<Posting>> Postings = new HashMap<>();
    private HashMap<String, TokenInfo> Tokens = new HashMap<>();
    private HashMap<Integer, List<Double>> DocumentVector = new HashMap<>();

    public InvertedIndex(String[] filePaths) {

        for (String filePath : filePaths) {
            N++;
            var source = new Source(N, filePath);
            this.Sources.put(N, source);
        }

        for (Source source : this.Sources.values()) {
            for (Map.Entry<String, Integer> entry : source.getWordsFreq().entrySet()) {
                String term = entry.getKey();
                int freq = entry.getValue();

                var tokenInfo = this.Tokens.getOrDefault(term, new TokenInfo());
                var postings = this.Postings.getOrDefault(term, new LinkedList<Posting>());

                postings.add(new Posting(source.getId(), freq));
                tokenInfo.inc();

                this.Tokens.put(term, tokenInfo);
                this.Postings.put(term, postings);
            }
        }

        Tokenize();
    }

    private void Tokenize() {
        for (Integer id : Sources.keySet()) {
            List<Double> tokenVector = new LinkedList<Double>();
            for (String word : Postings.keySet()) {
                tokenVector.add(TF(word, id) * IDF(word));
            }
            this.DocumentVector.put(id, tokenVector);
        }
    }

    private double TF(String Term, int id) {
        Source source = this.Sources.get(id);
        return source.term_freq(Term);
    }

    private double IDF(String Term) {
        double numberOfDocs = N;
        double docFreq = this.Tokens.get(Term).getDF();
        double idf = Math.log10(numberOfDocs / docFreq);
        return idf;
    }

    public void Print() {
        // Print tokens header
        System.out.println("Tokens:");
        for (String word : Postings.keySet()) {
            System.out.printf("%-12s", word); // left-align with 12-character width
        }
        System.out.println("\n" + "-".repeat(Postings.size() * 12));

        // Print document vectors aligned under tokens
        for (List<Double> tokenVector : DocumentVector.values()) {

            for (Double value : tokenVector) {
                System.out.printf("%-12.4f", value); // left-align float with 4 decimals
            }

            System.out.println();
        }

    }

    public List<Double> CreateEmbedings(String query) {
        String[] words = query.split("\\W+");
        System.out.println("\n" + "-".repeat(Postings.size() * 12));
        HashMap<String, Integer> WordTF = new HashMap<>();
        for (String word : words) {
            WordTF.put(word, WordTF.getOrDefault(word, 0) + 1);
        }
        List<Double> tokenVector = new LinkedList<Double>();
        for (String word : Postings.keySet()) {
            tokenVector.add(WordTF.getOrDefault(word, 0) * IDF(word));
        }
        for (Double value : tokenVector) {
            System.out.printf("%-12.4f", value); // left-align float with 4 decimals
        }
        System.out.println();
        return tokenVector;
    }
}
