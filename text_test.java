package com.jpmchase.mangetout.service.policy;

import com.jpmchase.mangetout.model.kendra.KendraDocument;
import com.jpmchase.mangetout.model.kendra.KendraAttribute;
import com.jpmchase.mangetout.model.policy.GcrmEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringBootTest
class PolicyIngesterServiceTest {

    @Autowired
    private PolicyIngesterService underTest;

    @Test
    void generateDocumentId_setsDocumentIdCorrectly() {
        GcrmEntry entry = new GcrmEntry();
        entry.setPublishedVersionSeriesId("someDocumentId");

        KendraDocument document = underTest.createDocument();
        underTest.generateDocumentId(entry, document);

        assertThat(document.getId()).isEqualTo("https://spp.gaiacloud.jpmchase.net/#/results?docId=someDocumentId&page=griddocument");
    }

    @Test
    void setSiteLevelAttributes_setsAttributesCorrectly() {
        KendraDocument document = underTest.createDocument();

        underTest.setSiteLevelAttributes(document);

        assertThat(document.getString(SECTIONS_ATTR)).isEqualTo("Policies");
        assertThat(document.getStringList(SITES_ATTR)).contains("Your expected site value here");
        assertThat(document.getString(SITE_EXTERNAL_ID_ATTR)).isEqualTo("gcrm");
    }

    @Test
    void fieldMapping_setsFieldsCorrectly() {
        GcrmEntry entry = new GcrmEntry();
        entry.setDocTitle("Policy Title");
        entry.setSummary("This is the policy summary.");

        KendraDocument document = underTest.createDocument();

        underTest.fieldMapping(entry, document);

        assertThat(document.getTitle()).isEqualTo("Policy Title");
        assertThat(document.getString(DOCUMENT_BODY_ATTR)).isEqualTo("This is the policy summary.");
    }

    @Test
    void keywordsBoosting_setsKeywordsCorrectly() {
        GcrmEntry entry = new GcrmEntry();
        entry.setDocId("12345");

        KendraDocument document = underTest.createDocument();

        underTest.keywordsBoosting(entry, document);

        assertThat(document.getString(KEYWORDS_ATTR)).isEqualTo("policies");
    }

    @Test
    void addBodyIfTitleMissing_setsTitleFromBody() {
        GcrmEntry entry = new GcrmEntry();
        entry.setSummary("First line of the summary.\nThis is the rest of the summary.");

        KendraDocument document = underTest.createDocument();

        underTest.addBodyIfTitleMissing(entry, document);

        assertThat(document.getTitle()).isEqualTo("First line of the summary.");
    }

    @Test
    void entryForPolicy_generatesCorrectDocument() {
        GcrmEntry entry = new GcrmEntry();
        entry.setPublishedVersionSeriesId("someDocumentId");
        entry.setDocTitle("Policy Title");
        entry.setSummary("This is the policy summary.");

        KendraAttribute<Instant> fetched = KendraAttribute.of(Instant.now());

        KendraDocument document = underTest.entryForPolicy(entry, fetched);

        assertThat(document.getId()).isEqualTo("https://spp.gaiacloud.jpmchase.net/#/results?docId=someDocumentId&page=griddocument");
        assertThat(document.getTitle()).isEqualTo("Policy Title");
        assertThat(document.getString(DOCUMENT_BODY_ATTR)).isEqualTo("This is the policy summary.");
        assertThat(document.getString(SECTIONS_ATTR)).isEqualTo("Policies");
    }
}
