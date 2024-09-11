import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public interface WebCrawlerService<T> {
    
    // Method that must be implemented by classes for extracting links from a page
    Collection<String> extractLinks(Document document);

    // Method that must be implemented by classes for extracting data from a page
    void extractPageData(Document document, T target);

    // Default crawl method with generic crawling logic
    default void crawl(String startUrl, int depth, String domain, 
                       Consumer<T> receiver, Supplier<T> supplier, 
                       Predicate<UrlDepthPair> shouldContinueCrawling) {
        
        Set<String> visitedUrls = new HashSet<>();
        final List<String> notAllowedExtensions = Arrays.asList(".png", ".css", ".js");
        Queue<UrlDepthPair> queue = new LinkedList<>();
        queue.add(new UrlDepthPair(startUrl, depth));

        while (!queue.isEmpty()) {
            UrlDepthPair current = queue.poll();

            // Check if we should continue crawling (based on custom predicates)
            if (!shouldContinueCrawling.test(current)) {
                break;
            }

            // Skip if already visited
            if (visitedUrls.contains(current.url)) {
                continue;
            }

            visitedUrls.add(current.url); // Mark URL as visited

            try {
                // Apply logic for extracting links and content
                Document document = Jsoup.connect(current.url).get();
                String robots = document.select("meta[name=robots]").attr("content");

                if (!robots.toLowerCase().contains("noindex")) {
                    T target = supplier.get();
                    target.setUrl(current.url);
                    extractPageData(document, target); // Call implementation-specific data extraction
                    receiver.accept(target);

                    if (current.depth > 1) {
                        // Extract links from the page and filter by domain
                        Collection<String> links = extractLinks(document); // Call implementation-specific link extraction
                        for (String nextUrl : links) {
                            if (nextUrl.contains(domain) && !visitedUrls.contains(nextUrl)) {
                                queue.add(new UrlDepthPair(nextUrl, current.depth - 1));
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Error fetching URL: " + e.getMessage());
            }
        }
    }
}
