package Assignment2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import Assignment2.Crawler.Crawler;
import Assignment2.Index.InvertedIndex;

public class Main {

    public static void main(String args[]) throws IOException {
        // Gets the path of the directory where the documments will be saved before
        // being processed
        String files = Paths.get("").toAbsolutePath().resolve("Documents").toString() + File.separator;
        // Creates the directory if it doesn't exists
        File file = new File(files);
        // creating the crawler
        Crawler crawler = new Crawler(files);
        // crawling for a url
        Crawler.crawl("https://en.wikipedia.org/wiki/List_of_pharaohs");
        // reseting the crawl so that it can crawl 10 more documents
        Crawler.resetCrawlCount();
        // again
        Crawler.crawl("https://en.wikipedia.org/wiki/Pharaoh");
        // saving in the documents directory
        crawler.saveDocuments();
        // prints the directory of the documents
        System.out.println(files);

        // gets the path to all the documents in the documents directory
        String[] fileList = Arrays
                .stream(file.list())
                .map(x -> files + x)
                .toArray(String[]::new);
        // passes the docuemnts to the inverted index
        InvertedIndex index = new InvertedIndex(fileList);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter query (or type 'exit' to quit): ");
            String query = scanner.nextLine();

            if (query.equalsIgnoreCase("exit")) {
                break;
            }
            // processes the query
            index.Query(query);
        }
        scanner.close();
    }
}
