package Assignment2.Index;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvertedIndex {
    // the number of documents passed
    private int N = 0;
    // a list of document sources that contains all the document information
    private Map<Integer, Source> Sources = new TreeMap<>();
    // the inverted index where the keyword has the
    // list of docuemnts with the id of the document and its term frequency
    private Map<String, List<Posting>> Postings = new TreeMap<>();
    // stores the word token info "its docuemnt frequency"
    private Map<String, TokenInfo> Tokens = new TreeMap<>();
    // the list of embeding for each document
    private Map<Integer, List<Double>> DocumentVector = new TreeMap<>();

    public InvertedIndex(String[] filePaths) {
        // stores the documment info in a source class instance
        for (String filePath : filePaths) {

            N++;
            var source = new Source(N, filePath);
            this.Sources.put(N, source);
        }

        for (Source source : this.Sources.values()) {
            // gets the word frequenct from each source
            for (Map.Entry<String, Integer> entry : source.getWordsFreq().entrySet()) {
                String term = entry.getKey();
                int freq = entry.getValue();
                // if the word has token info or a posting it fetches it
                // otherwise it creates the posting list for it
                var tokenInfo = this.Tokens.getOrDefault(term, new TokenInfo());
                var postings = this.Postings.getOrDefault(term, new LinkedList<Posting>());
                // updates the posting list and the token info
                postings.add(new Posting(source.getId(), freq));
                tokenInfo.inc();
                // saves the changes to the releavent hashmaps
                this.Tokens.put(term, tokenInfo);
                this.Postings.put(term, postings);
            }
        }
        // tokeninzeing
        Tokenize();
    }

    // generates the embding list for each document
    private void Tokenize() {

        for (Integer id : Sources.keySet()) {
            List<Double> tokenVector = new LinkedList<Double>();
            for (String word : Postings.keySet()) {
                tokenVector.add(TF(word, id) * IDF(word));
            }
            this.DocumentVector.put(id, tokenVector);
        }
    }

    // calculate the term frequency for each document
    private double TF(String Term, int id) {
        Source source = this.Sources.get(id);
        return source.term_freq(Term);
    }

    // calcutes the IDF for each term
    private double IDF(String Term) {
        double numberOfDocs = N;
        double docFreq = this.Tokens.get(Term).getDF();
        double idf = Math.log10(numberOfDocs / docFreq);
        return idf;
    }

    // querying
    public void Query(String Query) {
        // craetes an embeding for the query
        List<Double> QueryEmbeding = CreateEmbedings(Query);
        // stores the list of sources the have the same similarity value
        Map<Double, List<Source>> similarVectors = new TreeMap<>();
        for (var entry : DocumentVector.entrySet()) {
            // calculates the cosinesimilarity
            var similarityValue = CosineSimilarity(QueryEmbeding, entry.getValue());
            var sources = similarVectors.getOrDefault(-similarityValue, new LinkedList<>());
            sources.add(Sources.get(entry.getKey()));
            // storing the smiliarity value in -ve to flip the order inside the map
            similarVectors.put(-similarityValue, sources);
        }
        System.out.println("----------------------------------------------------------------");
        System.out.println(String.format("Querying : %s", Query));
        System.out.println("----------------------------------------------------------------");
        // printing the similar vectors sources
        for (var entry : similarVectors.entrySet()) {
            // fliping the entry value sign for display
            System.out.println(String.format("Similarity score : %.2f", (-entry.getKey())));
            for (Source source : entry.getValue()) {
                System.out.print("    ");
                System.out.println(source.getFilePath());
            }
        }
    }

    // caclculates the cosine similarity
    private double CosineSimilarity(List<Double> list1, List<Double> list2) {
        double norm1 = 0; // norm for the first list
        double norm2 = 0; // norm for the second list
        double dotProduct = 0; // the dot product of both of the lists
        for (int i = 0; i < list1.size(); i++) {
            norm1 += list1.get(i) * list1.get(i);
            norm2 += list2.get(i) * list2.get(i);
            dotProduct += list1.get(i) * list2.get(i);
        }
        if (norm1 == 0.0 || norm2 == 0.0)
            return 0.0;
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    // generates the embeding for any query
    private List<Double> CreateEmbedings(String query) {
        String[] words = query.toLowerCase().split("\\W+");
        Map<String, Integer> WordTF = new TreeMap<>();
        for (String word : words) {
            if (word.isEmpty())
                continue;
            WordTF.put(word, WordTF.getOrDefault(word, 0) + 1);
        }
        List<Double> tokenVector = new LinkedList<Double>();
        for (String word : Postings.keySet()) {
            var count = WordTF.getOrDefault(word, 0);
            var freq = count == 0 ? 0 : (1 + Math.log10(count));
            tokenVector.add(freq * IDF(word));
        }

        return tokenVector;
    }
}
