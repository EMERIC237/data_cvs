public class WebCrawlerExample {

    public static void main(String[] args) {
        WebCrawlerService<TestMutable> webCrawlerService = new CustomWebCrawlerService();

        // Define a Predicate that keeps crawling as long as the domain is "test.com" and limits depth to 100
        Predicate<UrlDepthPair> crawlUntilNoMoreTestComLinks = CrawlPredicates.combined(
            CrawlPredicates.depthLimit(100),        // Limit depth to 100
            CrawlPredicates.domainLimit("test.com") // Keep crawling as long as the domain is "test.com"
        );

        // Start the crawl process with a depth limit of 100, keep crawling as long as there are "test.com" links
        webCrawlerService.crawl(
            "https://www.test.com",  // Starting URL
            100,                     // Maximum depth of 100
            "test.com",              // Domain to keep crawling for
            target -> System.out.println("Crawled URL: " + target.getUrl()), // Receiver: Handle crawled data
            TestMutable::new,        // Supplier: Provide new instances of your target class
            crawlUntilNoMoreTestComLinks // Crawl conditions as predicate
        );
    }
}
