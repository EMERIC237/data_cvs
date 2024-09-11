public class WebCrawlerExample {

    public static void main(String[] args) {
        WebCrawlerService<TestMutable> webCrawlerService = new CustomWebCrawlerService();

        // Define a Predicate that limits the crawl depth to 5 and restricts the domain to "test.com"
        Predicate<UrlDepthPair> crawlDepth5AndDomainTestCom = CrawlPredicates.combined(
            CrawlPredicates.depthLimit(5),          // Limit depth to 5
            CrawlPredicates.domainLimit("test.com") // Restrict to domain "test.com"
        );

        // Start the crawl process with a depth of 5, restricted to domain "test.com"
        webCrawlerService.crawl(
            "https://www.test.com",  // Starting URL
            5,                       // Depth
            "test.com",              // Domain to restrict crawling
            target -> System.out.println("Crawled URL: " + target.getUrl()), // Receiver: Handle crawled data
            TestMutable::new,        // Supplier: Provide new instances of your target class
            crawlDepth5AndDomainTestCom // Crawl conditions as predicate
        );
    }
}
