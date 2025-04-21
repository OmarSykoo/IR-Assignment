package Assignment2.Index;

import lombok.Getter;

@Getter
public class Posting {
    private int docId;
    private int dtf = 1;

    public Posting(int id, int t) {
        docId = id;
        dtf = t;
    }

    public Posting(int id) {
        docId = id;
    }

    public void inc() {
        this.dtf++;
    }
}