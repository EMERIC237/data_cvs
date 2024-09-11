@Test
void crawl_shouldNotRevisitSameUrl() {
    enqueueResourceHtml("_path: \"crawler\", \"root.html\"");
    enqueueResourceHtml("_path: \"crawler\", \"childOne.html\"");

    ArrayList<TestMutable> target = new ArrayList<>();
    PageDataExtractor<TestMutable> dataExtractor = extractingFromPageUsing(TestMutable::new);
    
    // Simulate the first crawl
    webCrawlerService.crawl("root.html", 1, "localhost", target::add, dataExtractor);
    
    // Simulate a second crawl where the same URL is present
    webCrawlerService.crawl("root.html", 1, "localhost", target::add, dataExtractor);
    
    // Assert that the URL is not visited again, so the target size does not change
    assertThat(target, hasSize(1)); // Ensure only 1 unique URL is crawled
}

