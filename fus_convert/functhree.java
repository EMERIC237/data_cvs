package fus_convert;

public class functhree {
    public class DocumentProcessor {

        public Document processDocument(Document doc) {
            if (doc.getId() != null) {
                if (doc.hasField("title")) {
                    doc.addField("title_txt", doc.getFirstFieldValue("title"));
                }
    
                if (!doc.hasField("title_txt") || doc.getFirstFieldValue("title_txt").isEmpty()) {
                    String value = doc.getFirstFieldValue("body_t");
    
                    if (value != null) {
                        int startIndex = findFirstAlphabetIndex(value);
                        value = value.substring(startIndex);
    
                        int len = value.indexOf("\n") > 1 ? value.indexOf("\n") : 50;
                        value = value.substring(0, Math.min(len, value.length()));
                    }
    
                    doc.setField("title_txt", value);
                }
            }
            return doc;
        }
    
        private int findFirstAlphabetIndex(String text) {
            for (int i = 0; i < text.length(); i++) {
                if (Character.isLetter(text.charAt(i))) {
                    return i;
                }
            }
            return 0; // Default to start if no alphabetic character is found
        }
    }
    
    
}
