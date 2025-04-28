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
    // the max number of crawls needed to be performed before restarting the count
    private static final int MAX_DOCS_PER_CRAWL = 10;
    // the visited urls
    private static HashSet<String> visited = new HashSet<>();
    // the pages of the visited urls
    private static HashMap<String, String> pages = new HashMap<>();
    private static String MainUrl = "https://en.wikipedia.org/wiki/";
    // the crawl count that is to be reset when needed
    private static int currentCrawlCount = 0;

    public Crawler(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public static void crawl(String url) {
        // skips the the url that doesn't start with the main url
        // any url that contain # or : or ? are duplicates so they are filterd
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
            // stops crawling if the crawl count is reached
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

    // saves the docuemnt in the documents directory
    public void saveDocuments() {
        // checks if the directory exists and creates it if not
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        int count = 1;
        for (String url : pages.keySet()) {
            File outFile = new File(dir, String.format("%s.html", url.substring(MainUrl.length())));
            // logs the state of the saved file in case of success and failure
            try (FileWriter writer = new FileWriter(outFile)) {
                writer.write(pages.get(url));
                System.out.println("Saved: " + outFile.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Failed to save page" + count + ".html - " + e.getMessage());
            }
            count++;
        }
    }

    // resets the crawl count
    public static void resetCrawlCount() {
        currentCrawlCount = 0;
    }
}
