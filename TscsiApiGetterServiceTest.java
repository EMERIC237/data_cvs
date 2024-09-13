@SpringBootTest
public class TscsiApiGetterServiceTest implements WebCrawlerServiceTest<TscsiEntry> {

    @Autowired
    private TscsiApiGetterService underTest;

    @SpyBean
    @Getter
    private TscsiConfigProperties tscsiConfig;

    @SpyBean(name = "restTemplate")
    @Getter
    private RestTemplate restTemplate;

    @SpyBean(name = "webClient")
    @Getter
    private WebClient webClient;

    @Getter
    private MockWebServer mockWebServer;

    @BeforeEach
    void setUpMockServer() {
        mockWebServer = new MockWebServer();
        bindWebServerToService(TscsiApiGetterService.class);
    }

    @AfterEach
    void tearDownMockServer() {
        stopMockWebServer();
    }

    @Test
    void getAllUrl_returnsCompleteUriPrefixedWithBaseUri() {
        assertThat(underTest.getAllUrl(), looksCompletedFromConfig(tscsiConfig));
    }

    @Test
    void getAll_whenSystemShouldNotRun_callsNoClientsAndEmitsNoResults() {
        setupSpySystemShouldRun(tscsiConfig, result = false);
        final Consumer<TscsiEntry> receiver = mock(Consumer.class);

        underTest.getAll(receiver);

        verify(tscsiConfig, never()).shouldRun(anyString(), ArgumentMatchers.any(DecisionApproach.class));
        verifyNoInteractions(restTemplate);
        verifyNoInteractions(webClient);
        verifyNoInteractions(receiver);
    }

    @Test
    void getAll_whenApiSuccess_emitsParsedApiResults() {
        setupSpyFeatureShouldRun(tscsiConfig, "REACTIVE", result = true);
        enqueueResourceHtml("--path", "crawler/root.html"); // Add mock resource for crawling test

        final List<TscsiEntry> result = new ArrayList<>();

        underTest.getAll(result::add);

        assertThat(result, allOf(
            everyItem(instanceOf(TscsiEntry.class)),
            hasSize(greaterThan(1))
        ));

        verifyNoInteractions(webClient);
    }

    @Test
    void crawl_singlePage_withNoLinks_shouldReturnOnlyStartPage() {
        enqueueResourceHtml("--path", "crawler/root.html");

        ArrayList<TscsiEntry> target = new ArrayList<>();
        PageDataExtractor<TscsiEntry> dataExtractor = extractingFromPageUsing(
            TscsiEntry::setTitle, TscsiEntry::setBody
        );

        underTest.crawl("crawler/root.html", 1, "localhost", target::add, TscsiEntry::new,
            LinkExtractor.anchorReferenceLinks(), dataExtractor);

        assertThat(target, hasSize(1));
    }

    @Test
    void crawl_page_withLink_shouldCrawlLink() {
        enqueueResourceHtml("--path", "crawler/root.html");
        enqueueResourceHtml("--path", "crawler/childOne.html");

        ArrayList<TscsiEntry> target = new ArrayList<>();
        PageDataExtractor<TscsiEntry> dataExtractor = extractingFromPageUsing(
            TscsiEntry::setTitle, TscsiEntry::setBody
        );

        underTest.crawl("crawler/root.html", 2, "localhost", target::add, TscsiEntry::new,
            LinkExtractor.anchorReferenceLinks(), dataExtractor);

        assertThat(target, hasSize(2));
    }

    @Test
    void crawl_page_withNoIndexRobotsTag_shouldNotCrawl() {
        enqueueResourceHtml("--path", "crawler/emptyrobots.html");

        ArrayList<TscsiEntry> target = new ArrayList<>();
        PageDataExtractor<TscsiEntry> dataExtractor = extractingFromPageUsing(
            TscsiEntry::setTitle, TscsiEntry::setBody
        );

        underTest.crawl("crawler/emptyrobots.html", 1, "localhost", target::add, TscsiEntry::new,
            LinkExtractor.anchorReferenceLinks(), dataExtractor);

        assertThat(target, hasSize(0));
    }
}
