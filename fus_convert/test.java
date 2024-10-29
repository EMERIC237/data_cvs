@SpringBootTest
class TcscsiApiIngesterServiceTest {

    @MockBean
    private TcscsiApiGetterService getter;

    @MockBean
    private SenderService sender;

    @Autowired
    private TcscsiApiIngesterService underTest;

    private TscsiEntry entry;

    @BeforeEach
    void setUp() {
        entry = new TscsiEntry("title", "body", "url", "MISC_CATEGORY");
    }

    @Test
    void testPushAllBatched_GivenEmptyList_ShouldNotSendBatches() {
        underTest.pushAllBatched(sender);
        verify(sender, never()).sendBatch(any());
    }

    @Test
    void testPushAllBatched_GivenEntries_ShouldPushCorrectBatches() {
        when(getter.getAll(any())).thenReturn(List.of(entry));

        underTest.pushAllBatched(sender);

        verify(sender).sendBatch(argThat(batch -> {
            assertThat(batch.getDocuments()).hasSize(1);
            KendraDocument doc = batch.getDocuments().get(0);
            assertThat(doc.getTitle()).isEqualTo(entry.getTitle());
            assertThat(doc.getBody()).isEqualTo("<html>" + entry.getBody().trim() + "</html>");
            return true;
        }));
    }

    @Test
    void testSetSiteLevelAttributes_SetsAttributesCorrectly() {
        KendraDocument document = new KendraDocument();
        underTest.setSiteLevelAttributes(document);

        assertThat(document.getStringList("SECTIONS_ATTR")).contains("Policies");
        assertThat(document.getStringList("SITES_ATTR")).contains("Policies");
        assertThat(document.getString("SITE_EXTERNAL_ID_ATTR")).isEqualTo("gcrm");
    }

    @Test
    void testKeywordsBoosting_SetsCorrectKeywords() {
        KendraDocument document = new KendraDocument();
        underTest.keywordsBoosting(entry, document);

        assertThat(document.getStringList("KEYWORDS_ATTR")).contains("policies");
    }

    @Test
    void testAddBodyIfTitleMissing_AddsBodyWhenTitleIsMissing() {
        KendraDocument document = new KendraDocument();
        entry = new TscsiEntry(null, "body", "url", "MISC_CATEGORY");

        underTest.addBodyIfTitleMissing(entry, document);

        assertThat(document.getBody()).isEqualTo("<html>" + entry.getBody().trim() + "</html>");
    }
}
