import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Objects;

public final class ScalarMetric {
    static final String CHARACTERS_TO_SANITIZE = "._.*@!$=";
    static final String SANITIZED_CHARACTER = "-";
    
    private String shortKey;
    private String metricKey;
    private long count;

    public ScalarMetric(String shortKey, String metricKey, long count) {
        setShortKey(shortKey);
        setMetricKey(metricKey);
        setCount(count);
    }

    public String getShortKey() {
        return shortKey;
    }

    private void setShortKey(String shortKey) {
        this.shortKey = validateAndSanitizeUtf8(shortKey);
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getMetricKey() {
        return metricKey;
    }

    private void setMetricKey(String metricKey) {
        this.metricKey = validateAndSanitizeUtf8(metricKey.replaceAll("[._.*@!$=]", SANITIZED_CHARACTER));
    }

    // Step 1: Sanitization and UTF-8 validation logic
    private String sanitizeString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        // Replace all non-alphanumeric characters and special characters with underscores
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                         .replaceAll("[^\\p{ASCII}]", "_"); // Replace all non-ASCII characters
    }

    // Step 2: Complete UTF-8 validation
    private String validateAndSanitizeUtf8(String value) {
        if (value == null) {
            return null;
        }
        
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        
        // Check if the string is a valid UTF-8 string
        if (!StandardCharsets.UTF_8.newDecoder().canDecode(java.nio.ByteBuffer.wrap(bytes))) {
            throw new IllegalArgumentException("Invalid UTF-8 string");
        }
        
        // Apply sanitization logic to clean up unwanted characters
        return sanitizeString(new String(bytes, StandardCharsets.UTF_8));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScalarMetric that = (ScalarMetric) o;
        return count == that.count &&
               Objects.equals(shortKey, that.shortKey) &&
               Objects.equals(metricKey, that.metricKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shortKey, metricKey, count);
    }

    @Override
    public String toString() {
        return "ScalarMetric{shortKey='" + shortKey + "', metricKey='" + metricKey + "', count=" + count + "}";
    }
}
