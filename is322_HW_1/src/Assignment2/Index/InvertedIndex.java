package Assignment2.Index;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    public void Query(String Query) {
        List<Double> QueryEmbeding = CreateEmbedings(Query);
        Map<Double, List<Source>> similarVectors = new TreeMap<>();
        for (var entry : DocumentVector.entrySet()) {
            var similarityValue = CosineSimilarity(QueryEmbeding, entry.getValue());
            var sources = similarVectors.getOrDefault(-similarityValue, new LinkedList<>());
            sources.add(Sources.get(entry.getKey()));
            similarVectors.put(-similarityValue, sources);
        }
        System.out.println("----------------------------------------------------------------");
        System.out.println(String.format("Querying : %s", Query));
        System.out.println("----------------------------------------------------------------");
        for (var entry : similarVectors.entrySet()) {
            System.out.println(String.format("Similarity score : %.2f", (-entry.getKey())));
            for (Source source : entry.getValue()) {
                System.out.print("    ");
                System.out.println(source.getFilePath());
            }
        }
    }

    private double CosineSimilarity(List<Double> list1, List<Double> list2) {
        double norm1 = 0;
        double norm2 = 0;
        double dotProduct = 0;
        for (int i = 0; i < list1.size(); i++) {
            norm1 += list1.get(i) * list1.get(i);
            norm2 += list2.get(i) * list2.get(i);
            dotProduct += list1.get(i) * list2.get(i);
        }
        if (norm1 == 0.0 || norm2 == 0.0)
            return 0.0;
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    private List<Double> CreateEmbedings(String query) {
        String[] words = query.split("\\W+");
        HashMap<String, Integer> WordTF = new HashMap<>();
        for (String word : words) {
            WordTF.put(word, WordTF.getOrDefault(word, 0) + 1);
        }
        List<Double> tokenVector = new LinkedList<Double>();
        for (String word : Postings.keySet()) {
            tokenVector.add(WordTF.getOrDefault(word, 0) * IDF(word));
        }

        return tokenVector;
    }
}
