import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Objects;

public final class ScalarMetric {
    static final String SANITIZED_CHARACTER = "-";

    private String shortKey;
    private String metricKey;
    private long count;

    public ScalarMetric(String shortKey, String metricKey, long count) {
        // Sanitize the inputs here, ensuring the fields are always valid
        this.shortKey = validateAndSanitizeUtf8(shortKey);
        this.metricKey = validateAndSanitizeUtf8(metricKey.replaceAll("[._.*@!$=]", SANITIZED_CHARACTER));
        this.count = count;
    }

    public String getShortKey() {
        return shortKey;
    }

    public long getCount() {
        return count;
    }

    public String getMetricKey() {
        return metricKey;
    }

    private String sanitizeString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return Normalizer.normalize(input, Normalizer.Form.NFD)
                         .replaceAll("[^\\p{ASCII}]", "_");
    }

    private String validateAndSanitizeUtf8(String value) {
        if (value == null) {
            return null;
        }

        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        ByteBuffer byteBuffer = ByteBuffer.wrap(value.getBytes(StandardCharsets.UTF_8));

        try {
            CharBuffer charBuffer = decoder.decode(byteBuffer);
        } catch (CharacterCodingException e) {
            throw new IllegalArgumentException("Invalid UTF-8 string", e);
        }

        return sanitizeString(new String(byteBuffer.array(), StandardCharsets.UTF_8));
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
