import org.jsoup.nodes.Document;
import java.util.ArrayList;
import java.util.Collection;

public class CustomWebCrawlerService implements WebCrawlerService<TestMutable> {

    @Override
    public Collection<String> extractLinks(Document document) {
        // Implement link extraction logic here
        // For now, returning an empty list for demonstration purposes
        return new ArrayList<>();
    }

    @Override
    public void extractPageData(Document document, TestMutable target) {
        // Implement page data extraction logic here
        // For now, setting just the URL for demonstration purposes
        target.setUrl(document.location());
    }
}
