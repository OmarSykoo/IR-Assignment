package Assignment2;

import static java.lang.Math.subtractExact;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import Assignment2.Index.InvertedIndex;

public class Main {

    public static void main(String args[]) throws IOException {
        String files = Paths.get("").toAbsolutePath().resolve("Documents").toString() + File.separator;
        File file = new File(files);
        System.out.println(files);
        String[] fileList = Arrays
                .stream(file.list())
                .map(x -> files + x)
                .toArray(String[]::new);
        InvertedIndex index = new InvertedIndex(fileList);
        index.Print();
        String query1 = "cat sat";
        String query2 = "cat cat sat";
        index.CreateEmbedings(query1);
        index.CreateEmbedings(query2);
    }
}
