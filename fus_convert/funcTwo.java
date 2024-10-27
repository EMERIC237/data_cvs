package fus_convert;

public class funcTwo {
    import java.util.Objects;

public class DocumentProcessor {

    public Document processDocument(Document doc) {
        if (doc.getId() != null) {
            String toField = "filetype_t";
            String value = doc.getFirstFieldValue(toField);
            doc.addField("app_img_t", "");

            if (value != null) {
                if (value.endsWith("document")) {
                    doc.setField(toField, "Document");
                    doc.setField("app_img_t", "Document");
                } else if (value.endsWith("plain")) {
                    doc.setField(toField, "Text");
                    doc.setField("app_img_t", "Text");
                } else if (value.endsWith("html")) {
                    doc.setField(toField, "HTML");
                    doc.setField("app_img_t", "HTML");
                } else if (value.endsWith("pdf")) {
                    doc.setField(toField, "PDF");
                    doc.setField("app_img_t", "PDF");
                } else if (value.endsWith("sheet")) {
                    doc.setField(toField, "Spreadsheet");
                    doc.setField("app_img_t", "Spreadsheet");
                } else if (value.endsWith("ms-excel")) {
                    doc.setField(toField, "Spreadsheet");
                    doc.setField("app_img_t", "Spreadsheet");
                } else if (value.endsWith("msword")) {
                    doc.setField(toField, "Document");
                    doc.setField("app_img_t", "Document");
                } else if (value.endsWith("presentation")) {
                    doc.setField(toField, "Presentation");
                    doc.setField("app_img_t", "Presentation");
                } else {
                    doc.setField(toField, "Other");
                    doc.setField("app_img_t", "Other");
                }
            }

            String docId = doc.getId();
            if (docId.contains("https://mytechhub.jpmchase.net")) {
                doc.setField("app_img_t", "mytechhub");
            } else if (docId.contains("https://me.jpmchase.net")) {
                doc.setField("app_img_t", "mejpmc");
            } else if (docId.contains("https://databook.jpmchase.net:8082")) {
                doc.setField("app_img_t", "databook");
            } else if (docId.contains("https://sealdi.jpmchase.net:8080")) {
                doc.setField("app_img_t", "sealdi");
            } else if (docId.contains("https://videoportal.jpmchase.net")) {
                doc.setField("app_img_t", "videoportal");
            }

            String valueType = doc.getFirstFieldValue(toField);
            doc.setField("filetype_s", valueType);
        }
        return doc;
    }
}

}
