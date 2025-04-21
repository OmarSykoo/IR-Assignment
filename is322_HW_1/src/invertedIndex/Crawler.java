package invertedIndex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
    private String directoryPath;
    private static final int MAX_DOCS_PER_CRAWL = 10;

    private static HashSet<String> visited = new HashSet<>();
    private static HashMap<String, String> pages = new HashMap<>();

    private static int currentCrawlCount = 0;

    public Crawler(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public static void crawl(String url, int depth) {
        if (depth == 0 || visited.contains(url) || !url.startsWith("https://en.wikipedia.org/")
                || currentCrawlCount >= MAX_DOCS_PER_CRAWL)
            return;

        try {
            visited.add(url);
            Document doc = Jsoup.connect(url).get();
            System.out.println("Title: " + doc.title());

            pages.put(url, doc.html());
            currentCrawlCount++;

            if (currentCrawlCount >= MAX_DOCS_PER_CRAWL)
                return;

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                crawl(nextUrl, depth - 1);
                if (currentCrawlCount >= MAX_DOCS_PER_CRAWL)
                    break;
            }
        } catch (Exception e) {
            System.out.println("Failed to crawl " + url + " - " + e.getMessage());
        }
    }

    public void saveDocuments() {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        int count = 1;
        for (String url : pages.keySet()) {
            File outFile = new File(dir, "page" + count + ".html");
            try (FileWriter writer = new FileWriter(outFile)) {
                writer.write(pages.get(url));
                System.out.println("Saved: " + outFile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Failed to save page" + count + ".html - " + e.getMessage());
            }
            count++;
        }
    }

    public static void resetCrawlCount() {
        currentCrawlCount = 0;
    }
}
