import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WebcrawlerServiceImplTest {

    @Test
    public void testCrawl_singlePage_noLinks() throws IOException {
        // Mock the Jsoup connection and document
        Document mockDocument = Mockito.mock(Document.class);
        when(mockDocument.html()).thenReturn("<html></html>");
        when(mockDocument.select("a[href]")).thenReturn(new Elements());

        // Mock the Jsoup static connect method
        Jsoup jsoup = mockStatic(Jsoup.class);
        when(Jsoup.connect(anyString())).thenReturn(mockDocument);

        WebcrawlerServiceImpl webcrawlerService = new WebcrawlerServiceImpl();
        List<String> contents = webcrawlerService.crawl("http://example.com", 1, "example.com");

        assertEquals(1, contents.size());
        assertEquals("<html></html>", contents.get(0));
    }

    @Test
    public void testCrawl_singlePage_withLinks() throws IOException {
        // Mock the Jsoup connection and document
        Document mockDocument = Mockito.mock(Document.class);
        when(mockDocument.html()).thenReturn("<html></html>");

        Element mockElement = Mockito.mock(Element.class);
        when(mockElement.absUrl("href")).thenReturn("http://example.com/page1");

        Elements mockElements = new Elements();
        mockElements.add(mockElement);
        when(mockDocument.select("a[href]")).thenReturn(mockElements);

        // Mock the Jsoup static connect method
        Jsoup jsoup = mockStatic(Jsoup.class);
        when(Jsoup.connect(anyString())).thenReturn(mockDocument);

        WebcrawlerServiceImpl webcrawlerService = new WebcrawlerServiceImpl();
        List<String> contents = webcrawlerService.crawl("http://example.com", 2, "example.com");

        assertEquals(1, contents.size());
        assertEquals("<html></html>", contents.get(0));
    }

    @Test
    public void testCrawl_depthLimit() throws IOException {
        // Mock the Jsoup connection and document
        Document mockDocument = Mockito.mock(Document.class);
        when(mockDocument.html()).thenReturn("<html></html>");

        Element mockElement = Mockito.mock(Element.class);
        when(mockElement.absUrl("href")).thenReturn("http://example.com/page1");

        Elements mockElements = new Elements();
        mockElements.add(mockElement);
        when(mockDocument.select("a[href]")).thenReturn(mockElements);

        // Mock the Jsoup static connect method
        Jsoup jsoup = mockStatic(Jsoup.class);
        when(Jsoup.connect(anyString())).thenReturn(mockDocument);

        WebcrawlerServiceImpl webcrawlerService = new WebcrawlerServiceImpl();
        List<String> contents = webcrawlerService.crawl("http://example.com", 1, "example.com");

        assertEquals(1, contents.size());
        assertEquals("<html></html>", contents.get(0));
    }

    @Test
    public void testCrawl_skipDuplicates() throws IOException {
        // Mock the Jsoup connection and document
        Document mockDocument = Mockito.mock(Document.class);
        when(mockDocument.html()).thenReturn("<html></html>");

        Element mockElement = Mockito.mock(Element.class);
        when(mockElement.absUrl("href")).thenReturn("http://example.com");

        Elements mockElements = new Elements();
        mockElements.add(mockElement);
        when(mockDocument.select("a[href]")).thenReturn(mockElements);

        // Mock the Jsoup static connect method
        Jsoup jsoup = mockStatic(Jsoup.class);
        when(Jsoup.connect(anyString())).thenReturn(mockDocument);

        WebcrawlerServiceImpl webcrawlerService = new WebcrawlerServiceImpl();
        List<String> contents = webcrawlerService.crawl("http://example.com", 2, "example.com");

        assertEquals(1, contents.size());
        assertEquals("<html></html>", contents.get(0));
    }
}
