@Test
void crawl_singlePage_noLinks() {
    enqueueResourceHtml("_path: \"crawler\", \"childOne.html\"");
    ArrayList<TestMutable> target = new ArrayList<>();
    PageDataExtractor<TestMutable> dataExtractor = extractingFromPageUsing(TestMutable::new);
    webCrawlerService.crawl(url, depth: 3, domain: "localhost", target::add, TestMutable::new, LinkExtractor.anchorReferenceLinks(), dataExtractor);
    assertThat(target, hasSize(1));
}
/////// after
@Test
void crawl_singlePage_noLinks() {
    // Setup mock web server and HTML resources
    enqueueResourceHtml("_path: \"crawler\", \"childOne.html\"");

    // Initialize the target list and data extractor
    ArrayList<TestMutable> target = new ArrayList<>();
    PageDataExtractor<TestMutable> dataExtractor = extractingFromPageUsing(TestMutable::new);

    // Initialize an empty set for visited start links
    Set<String> visitedStartLinks = new HashSet<>();

    // Call the crawl method with visitedStartLinks
    webCrawlerService.crawl(url, 3, "localhost", target::add, TestMutable::new, 
                            LinkExtractor.anchorReferenceLinks(), dataExtractor, visitedStartLinks);

    // Assert the result
    assertThat(target, hasSize(1));
}


// signle_with)link
@Test
void crawl_singlePage_withLink() {
    enqueueResourceHtml("_path: \"crawler\", \"root.html\"");
    enqueueResourceHtml("_path: \"crawler\", \"childOne.html\"");
    ArrayList<TestMutable> target = new ArrayList<>();
    PageDataExtractor<TestMutable> dataExtractor = extractingFromPageUsing(TestMutable::new);
    webCrawlerService.crawl(url, depth: 2, domain: "localhost", target::add, TestMutable::new, LinkExtractor.anchorReferenceLinks(), dataExtractor);
    assertThat(target, hasSize(2));
}

/// update:
@Test
void crawl_singlePage_withLink() {
    // Setup mock web server and HTML resources
    enqueueResourceHtml("_path: \"crawler\", \"root.html\"");
    enqueueResourceHtml("_path: \"crawler\", \"childOne.html\"");

    // Initialize the target list and data extractor
    ArrayList<TestMutable> target = new ArrayList<>();
    PageDataExtractor<TestMutable> dataExtractor = extractingFromPageUsing(TestMutable::new);

    // Initialize an empty set for visited start links
    Set<String> visitedStartLinks = new HashSet<>();

    // Call the crawl method with visitedStartLinks
    webCrawlerService.crawl(url, 2, "localhost", target::add, TestMutable::new, 
                            LinkExtractor.anchorReferenceLinks(), dataExtractor, visitedStartLinks);

    // Assert the result
    assertThat(target, hasSize(2));
}
//// origibal
@Test
void crawl_noPage_withNoIndexRobotsTag_shouldNotIndexThePage() {
    enqueueResourceHtml("_path: \"crawler\", \"childOne.html\"");
    ArrayList<TestMutable> target = new ArrayList<>();
    PageDataExtractor<TestMutable> dataExtractor = extractingFromPageUsing(TestMutable::new);
    webCrawlerService.crawl(url, depth: 3, domain: "localhost", target::add, TestMutable::new, LinkExtractor.anchorReferenceLinks(), dataExtractor);
    assertThat(target, hasSize(0));
}

// update tets:
@Test
void crawl_noPage_withNoIndexRobotsTag_shouldNotIndexThePage() {
    // Setup mock web server and HTML resources
    enqueueResourceHtml("_path: \"crawler\", \"childOne.html\"");

    // Initialize the target list and data extractor
    ArrayList<TestMutable> target = new ArrayList<>();
    PageDataExtractor<TestMutable> dataExtractor = extractingFromPageUsing(TestMutable::new);

    // Initialize an empty set for visited start links
    Set<String> visitedStartLinks = new HashSet<>();

    // Call the crawl method with visitedStartLinks
    webCrawlerService.crawl(url, 3, "localhost", target::add, TestMutable::new, 
                            LinkExtractor.anchorReferenceLinks(), dataExtractor, visitedStartLinks);

    // Assert the result
    assertThat(target, hasSize(0));  // No links should be indexed due to 'noindex' tag
}
///