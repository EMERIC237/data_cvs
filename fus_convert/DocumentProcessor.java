package fus_convert;

public class funcOne {
    
    import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class DocumentProcessor {

    public Document processDocument(Document doc) {
        if (doc.getId() != null) {
            String fromField = "body_t";
            String toField = "preview_t";
            String value = doc.getFirstFieldValue(fromField);

            if (value != null) {
                // Remove unwanted characters (uncomment if needed)
                // value = value.replaceAll("[^\\w\\s]", "");
                // value = value.replaceAll("\r\n|\n|\t", "");

                // Define pattern for newline and tab characters
                Pattern pattern = Pattern.compile("[\\n\\t]");
                Matcher matcher = pattern.matcher(value);
                value = matcher.replaceAll(" ");

                doc.setField(fromField, value);

                int length = value.length() < 500 ? value.length() : 500;
                value = value.substring(0, length);
                doc.addField(toField, value);
            }
        }
        return doc;
    }
}

}
