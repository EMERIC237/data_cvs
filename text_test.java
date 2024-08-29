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
      private GcrmEntry entry;

    @BeforeEach
    void setUp() {
        entry = new GcrmEntry(
            List.of("definedTerm1", "definedTerm2"),
            "subLOBValue",
            "prevDocVSID",
            "workflowStatus",
            "managerSid",
            "effectiveDate",
            "changeDate",
            "lobHierarchyValue",
            "docTitleValue",
            "summaryValue",
            "filterCountry",
            "boostdocsValue",
            "countryValue",
            new Date(),
            "publishedVersionSeriesIdValue",
            "docIdValue",
            "modifiedOnValue",
            "mimeTypeValue"
        );
    }

    @Test
void pushAllBatched_givenEntries_pushesBatchesCorrectly() throws IOException {
    setupGetAllEmits(entry);

    underTest.pushAllBatched(sender);

    verify(sender).sendBatch(assertArg(b -> {
        assertThat(b.possibleAttributes()).containsKeys(
            FETCHED_AT_ATTR, KEYWORDS_ATTR, DESCRIPTION_ATTR, 
            ApplicationJsonConstants.SITES_ATTR, 
            ApplicationJsonConstants.SECTIONS_ATTR, 
            ApplicationJsonConstants.SITE_EXTERNAL_ID_ATTR
        );
        assertThat(b.documents()).hasSize(1);

        final KendraDocument doc = b.documents().get(0);
        assertThat(doc.getTitle()).isEqualTo(entry.docTitle());
        assertThat(doc.getString(SITE_EXTERNAL_ID_ATTR)).isEqualTo("gcrm");
        assertThat(doc.getStringList(SITES_ATTR)).contains("Corporate & Investment Banking");
    }));
}

    @Test
void generateDocumentId_setsDocumentIdCorrectly() {
    KendraDocument document = underTest.createDocument();

    underTest.generateDocumentId(entry, document);

    String expectedUrl = "https://spp.gaiacloud.jpmchase.net/#/results?docId=" + 
                          URLEncoder.encode(entry.docId().toLowerCase(), StandardCharsets.UTF_8) +
                          "&page=griddocument";
    assertThat(document.getId()).isEqualTo(expectedUrl);
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
    KendraDocument document = underTest.createDocument();

    underTest.fieldMapping(entry, document);

    assertThat(document.getTitle()).isEqualTo(entry.docTitle());
    assertThat(document.getString(DOCUMENT_BODY_ATTR)).isEqualTo(entry.summary());
}



    @Test
void keywordsBoosting_setsCorrectKeywords() {
    KendraDocument document = underTest.createDocument();

    underTest.keywordsBoosting(entry, document);

    assertThat(document.getString(KEYWORDS_ATTR)).isEqualTo("policies");
}


   @Test
void addBodyIfTitleMissing_addsBodyWhenTitleIsMissing() {
    KendraDocument document = underTest.createDocument();

    // Simulate a missing title in the entry
    GcrmEntry entryWithoutTitle = new GcrmEntry(
        entry.definedTerms(),
        entry.subLOB(),
        entry.immediatePrevDocVSID(),
        entry.workflowStatus(),
        entry.docManagerSid(),
        entry.initialEffectiveDate(),
        entry.statusChangeDate(),
        entry.lobHierarchy(),
        null, // docTitle is missing
        entry.summary(),
        entry.filter_country(),
        entry.boostdocs(),
        entry.country(),
        entry.publishedDate(),
        entry.publishedVersionSeriesId(),
        entry.docId(),
        entry.modifiedOn(),
        entry.mimeType()
    );

    underTest.addBodyIfTitleMissing(entryWithoutTitle, document);

    assertThat(document.getTitle()).isEqualTo("summaryValue".substring(0, Math.min("summaryValue".length(), 50)));
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
