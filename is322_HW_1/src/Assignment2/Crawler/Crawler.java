package Assignment2.Crawler;

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
    private static String MainUrl = "https://en.wikipedia.org/";
    private static int currentCrawlCount = 0;

    public Crawler(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public static void crawl(String url) {
        if (visited.contains(url) ||
                !url.startsWith(MainUrl) ||
                currentCrawlCount >= MAX_DOCS_PER_CRAWL ||
                url.substring(MainUrl.length()).contains("#") ||
                url.substring(MainUrl.length()).contains(":") ||
                url.substring(MainUrl.length()).contains("?"))
            return;

        try {
            visited.add(url);
            Document doc = Jsoup.connect(url).get();
            Element content = doc.selectFirst("#mw-content-text");
            if (content == null || content.text().length() < 200) // Ignore very short or no content
                return;
            System.out.println("Title: " + doc.title());
            System.out.println("Url: " + url);

            pages.put(url, doc.html());
            currentCrawlCount++;

            if (currentCrawlCount >= MAX_DOCS_PER_CRAWL)
                return;

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                crawl(nextUrl);
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
