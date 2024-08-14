package com.jpmchase.mangetout.service.intranet;

import com.jpmchase.mangetout.model.intranet.IntranetEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
    void pushAllBatched_givenNone_pushesNoBatches() throws IOException {
        when(intranetGetter.getDATA()).thenReturn(List.of());

        underTest.pushAllBatched(sender);

        verify(sender, never()).sendBatch(any());
    }

    @Test
    void pushAllBatched_givenEntries_pushesBatchesCorrectly() throws IOException {
        IntranetEntry entry = new IntranetEntry("url_t", "body_t", "title_t", "abstract_t", "site", "site_url", 
                                                List.of("user1", "user2"), "lob_ss", "id", "content_dt", 
                                                "end_dt", 1, 12345, "start_dt");

        when(intranetGetter.getDATA()).thenReturn(List.of(entry));

        underTest.pushAllBatched(sender);

        verify(sender).sendBatch(assertArg(b -> {
            assertThat(b.possibleAttributes()).containsKeys(FETCHED_AT_ATTR, KEYWORDS_ATTR, DESCRIPTION_ATTR);
            assertThat(b.documents()).hasSize(1);

            final KendraDocument doc = b.documents().get(0);
            assertThat(doc.getId()).isEqualTo(entry.id());
            assertThat(doc.getTitle()).isEqualTo(entry.title_t());
            assertThat(doc.getString("site")).isEqualTo(entry.site());
            assertThat(doc.getStringList("filter_country")).contains("All");
            assertThat(doc.getString("_lw_data_source_s")).isEqualTo("DeleteGlobalPagesDS");
            assertThat(doc.getString("_lw_data_source_collection_s")).isEqualTo("global");
        }));
    }

    @Test
    void fieldMapping_setsFieldsCorrectly() {
        KendraDocument document = new KendraDocument(Map.of());
        IntranetEntry entry = new IntranetEntry("url_t", "body_t", "title_t", "abstract_t", "site", "site_url", 
                                                List.of("user1", "user2"), "lob_ss", "id", "content_dt", 
                                                "end_dt", 1, 12345, "start_dt");

        underTest.fieldMapping(entry, document);

        assertThat(document.getTitle()).isEqualTo(entry.title_t());
        assertThat(document.getString("site")).isEqualTo(entry.site());
        assertThat(document.getStringList("filter_country")).contains("All");
        // New checks for attributes declared in ApplicationJsonConstants
        assertThat(document.getAttribute("SITE")).isEqualTo(attributeWith(SITE, entry.site()));
    }

    @Test
    void deleteDraftDocument_setsFieldsForDraft() {
        KendraDocument document = new KendraDocument(Map.of());
        IntranetEntry entry = new IntranetEntry("url_t", "body_t", "title_t", "abstract_t", "site", "site_url", 
                                                List.of("user1", "user2"), "lob_ss", "id", "content_dt", 
                                                "end_dt", 1, 12345, "start_dt");

        underTest.deleteDraftDocument(entry, document);

        assertThat(document.getString("_lw_data_source_s")).isEqualTo("DeleteGlobalPagesDS");
        assertThat(document.getString("_lw_data_source_collection_s")).isEqualTo("global");
    }

    @Test
    void deleteDraftDocument_url_setsFieldsForDraftUrl() {
        KendraDocument document = new KendraDocument(Map.of());
        IntranetEntry entry = new IntranetEntry("url_t", "body_t", "title_t", "abstract_t", "site", "site_url", 
                                                List.of("user1", "user2"), "lob_ss", "id", "content_dt", 
                                                "end_dt", 1, 12345, "start_dt");

        underTest.deleteDraftDocument_url(entry, document);

        assertThat(document.getString("_lw_data_source_s")).isEqualTo("DeleteGlobalPagesDS");
        assertThat(document.getString("_lw_data_source_collection_s")).isEqualTo("global");
    }

    @Test
    void addPreviewText_setsPreviewTextCorrectly() {
        KendraDocument document = new KendraDocument(Map.of());
        IntranetEntry entry = new IntranetEntry("url_t", "body_t with content", "title_t", "abstract_t", "site", 
                                                "site_url", List.of("user1", "user2"), "lob_ss", "id", 
                                                "content_dt", "end_dt", 1, 12345, "start_dt");

        underTest.addPreviewText(entry, document);

        assertThat(document.getString("body_t")).isEqualTo("body_t with content");
        assertThat(document.getString("preview_t")).isEqualTo("body_t with content".substring(0, 150));
        // New check for attribute from ApplicationJsonConstants
        assertThat(document.getAttribute("PREVIEW_TEXT")).isEqualTo(attributeWith(PREVIEW_TEXT, entry.body_t().substring(0, 150)));
    }

    @Test
    void checkAndSetSiteForContent_setsLobNameCorrectly() {
        KendraDocument document = new KendraDocument(Map.of());
        String site = "cibhome";

        String lobName = underTest.checkAndSetSiteForContent(document, site);

        assertThat(lobName).isEqualTo("Corporate & Investment Bank");
        assertThat(document.getString("site_ss")).isEqualTo("Corporate & Investment Bank");
        assertThat(document.getString("site")).isEqualTo("O365_cib");
        assertThat(document.getAttribute("SITE_ID")).isEqualTo(attributeWith(SITE_ID, "O365_cib"));
    }



    @Test
    void setSiteLevelAttributes_setsContentTypeAndTitleCorrectly() {
        // Arrange
        KendraDocument document = underTest.createDocument();
        IntranetEntry entry = new IntranetEntry(
            "https://sites.jpmchase.com/sites/todayjpmc/pages/index.aspx#/profile/somepage",
            "body_t content",
            "title_t",
            "abstract_t",
            "site",
            "site_url",
            List.of("user1", "user2"),
            "lob_ss",
            "id",
            "content_dt",
            "end_dt",
            1,
            12345,
            "start_dt"
        );

        // Act
        underTest.setSiteLevelAttributes(entry, document);

        // Assert
        assertThat(document.getString("contentType_s")).isEqualTo("news");
        assertThat(document.getAttribute(CONTENT_TYPE).getValue()).isEqualTo("news");
    }

    @Test
    void setSiteLevelAttributes_setsLobNameAndTitleCorrectly() {
        // Arrange
        KendraDocument document = underTest.createDocument();
        IntranetEntry entry = new IntranetEntry(
            "docIdValue",
            "body_t content",
            "title_t",
            "abstract_t",
            "site",
            "site_url",
            List.of("user1", "user2"),
            "lob_ss",
            "id",
            "content_dt",
            "end_dt",
            1,
            12345,
            "start_dt"
        );

        // Act
        underTest.setSiteLevelAttributes(entry, document);

        // Assert
        String lobName = underTest.checkAndSetSiteForContent(document, "cohohome");
        assertThat(document.getTitle()).isEqualTo("title_t -- (The Firm)");
        assertThat(document.getStringList("LOBS")).contains("Company Home");
    }

    @Test
    void setSiteLevelAttributes_setsSiteAndFileTypeCorrectly() {
        // Arrange
        KendraDocument document = underTest.createDocument();
        IntranetEntry entry = new IntranetEntry(
            "docIdValue",
            "body_t content",
            "title_t",
            "abstract_t",
            "rmhome",
            "site_url",
            List.of("user1", "user2"),
            "lob_ss",
            "id",
            "content_dt",
            "end_dt",
            1,
            12345,
            "start_dt"
        );

        // Act
        underTest.setSiteLevelAttributes(entry, document);

        // Assert
        assertThat(document.getAttribute(FILE_TYPE_ATTR).getValue()).isEqualTo("HTML");
        assertThat(document.getAttribute(SITE_DATA).getValue()).isEqualTo("site");
    }
}


