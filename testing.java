package com.jpmchase.mangetout.service.intranet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class IntranetIngesterServiceTest {

    @MockBean
    private IntranetDataGetterService intranetGetter;

    @MockBean
    private SenderService sender;

    @Autowired
    private IntranetIngesterService underTest;

    @Test
    void underTest_whenSpringBooted_exists() {
        assertThat(underTest).isInstanceOf(IntranetIngesterService.class);
    }

    @Test
    void pushAllBatched_givenNone_pushesNoBatches() {
        when(intranetGetter.getAll()).thenReturn(List.of());

        underTest.pushAllBatched(sender);

        verify(sender).sendBatch(assertArg(b -> {
            assertThat(b.possibleAttributes()).containsKeys(FETCHED_AT_ATTR, KEYWORDS_ATTR, DESCRIPTION_ATTR);
            assertThat(b.documents()).isEmpty();
        }));
    }

    @Test
    void pushUpdated_givenAnEntry_producesBatchOfOne() {
        final IntranetEntry entry = new IntranetEntry("title", "site", "body", "id", "site_url", "lob_ss");
        final Instant createdAt = Instant.now();

        when(intranetGetter.getAll()).thenReturn(List.of(entry));

        underTest.pushAllBatched(sender);

        verify(sender).sendBatch(assertArg(b -> {
            assertThat(b.possibleAttributes()).containsKeys(FETCHED_AT_ATTR, KEYWORDS_ATTR, DESCRIPTION_ATTR);
            assertThat(b.documents()).hasSize(1);

            final KendraDocument doc = b.documents().get(0);
            assertThat(doc.getId()).isEqualTo(entry.id());
            assertThat(doc.getTitle()).isEqualTo(entry.title_t());
            assertThat(doc.getString("site")).isEqualTo(entry.site());
            assertThat(doc.getStringList("filter_country")).contains("All");
            assertThat(doc.getDate(FETCHED_AT_ATTR)).isNotNull();
            assertThat(doc.getString("_lw_data_source_s")).isEqualTo("DeleteGlobalPagesDS");
            assertThat(doc.getString("_lw_data_source_collection_s")).isEqualTo("global");
        }));
    }

    @Test
    void fieldMapping_setsFieldsCorrectly() {
        final KendraDocument document = new KendraDocument(Map.of());
        final IntranetEntry entry = new IntranetEntry("title", "site", "body", "id", "site_url", "lob_ss");

        underTest.fieldMapping(entry, document);

        assertThat(document.getTitle()).isEqualTo(entry.title_t());
        assertThat(document.getString("site")).isEqualTo(entry.site());
        assertThat(document.getStringList("filter_country")).contains("All");
    }

    @Test
    void deleteDraftDocument_setsFieldsForDraft() {
        final KendraDocument document = new KendraDocument(Map.of());
        final IntranetEntry entry = new IntranetEntry("title", "site", "body", "id", "site_url", "lob_ss");

        underTest.deleteDraftDocument(entry, document);

        assertThat(document.getString("_lw_data_source_s")).isEqualTo("DeleteGlobalPagesDS");
        assertThat(document.getString("_lw_data_source_collection_s")).isEqualTo("global");
    }

    @Test
    void deleteDraftDocument_url_setsFieldsForDraftUrl() {
        final KendraDocument document = new KendraDocument(Map.of());
        final IntranetEntry entry = new IntranetEntry("title", "site", "body", "id", "site_url", "lob_ss");

        underTest.deleteDraftDocument_url(entry, document);

        assertThat(document.getString("_lw_data_source_s")).isEqualTo("DeleteGlobalPagesDS");
        assertThat(document.getString("_lw_data_source_collection_s")).isEqualTo("global");
    }

    @Test
    void addPreviewText_setsPreviewTextCorrectly() {
        final KendraDocument document = new KendraDocument(Map.of());
        final IntranetEntry entry = new IntranetEntry("title", "site", "body with content", "id", "site_url", "lob_ss");

        underTest.addPreviewText(entry, document);

        assertThat(document.getString("body_t")).isEqualTo("body with content");
        assertThat(document.getString("preview_t")).isEqualTo("body with content".substring(0, 150));
    }

    @Test
    void checkAndSetSiteForContent_setsLobNameCorrectly() {
        final KendraDocument document = new KendraDocument(Map.of());
        final String site = "cibhome";

        String lobName = underTest.checkAndSetSiteForContent(document, site);

        assertThat(lobName).isEqualTo("Corporate & Investment Bank");
        assertThat(document.getString("site_id")).isEqualTo("O365_cib");
    }
}
