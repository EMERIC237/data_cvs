@Test
void pushAllBatched_givenEntries_pushesBatchesCorrectly() throws IOException {
    // Given
    GcrmEntry entry = new GcrmEntry(
        "url_t", "body_t", "docTitle", "summary", "gcrm", 
        "site", "site_url", "content_dt", "end_dt", 
        1, 123456, "start_dt"
    );

    setupGetAllEmits(entry);

    // When
    underTest.pushAllBatched(sender);

    // Then
    verify(sender).sendBatch(assertArg(b -> {
        assertThat(b.possibleAttributes()).containsKeys(
            KEYWORDS_ATTR, FETCHED_AT_ATTR, 
            SITES_ATTR, SECTIONS_ATTR, SITE_EXTERNAL_ID_ATTR
        );
        assertThat(b.documents()).hasSize(1);

        final KendraDocument doc = b.documents().get(0);
        assertThat(doc.getId()).isEqualTo(entry.publishedVersionSeriesId());
        assertThat(doc.getTitle()).isEqualTo(entry.docTitle());
        assertThat(doc.getStringList(SITES_ATTR)).contains("gcrm");
        assertThat(doc.getStringList(SECTIONS_ATTR)).contains("Policies");
        assertThat(doc.getString(SITE_EXTERNAL_ID_ATTR)).isEqualTo("gcrm");
    }));
}
