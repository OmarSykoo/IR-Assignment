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
        String files = Paths.get("").toAbsolutePath().resolve("Documents").toString() + File.separator;
        File file = new File(files);
        Crawler crawler = new Crawler(files);
        Crawler.crawl("https://en.wikipedia.org/wiki/List_of_pharaohs");
        Crawler.resetCrawlCount();
        Crawler.crawl("https://en.wikipedia.org/wiki/Pharaoh");
        crawler.saveDocuments();
        System.out.println(files);
        String[] fileList = Arrays
                .stream(file.list())
                .map(x -> files + x)
                .toArray(String[]::new);
        InvertedIndex index = new InvertedIndex(fileList);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter query (or type 'exit' to quit): ");
            String query = scanner.nextLine();

            if (query.equalsIgnoreCase("exit")) {
                break;
            }

            index.Query(query);
        }
        scanner.close();
    }
}
