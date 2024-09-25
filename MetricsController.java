import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
public class MetricsController {

    private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);

    private final MetricsRepository metricsRepository;

    static final String HELP_TEXT = "# HELP %s Count of successful short key hits\n";
    static final String TYPE_TEXT = "# TYPE %s counter\n";
    static final String METRIC_TEXT = "%s{short_key=\"%s\"} %d\n";

    public MetricsController(MetricsRepository metricsRepository) {
        this.metricsRepository = metricsRepository;
    }

    @GetMapping(value = "/metrics")
    public void getMetrics(HttpServletResponse response) {
        response.setContentType(MediaType.TEXT_PLAIN.toString());

        if (metricsRepository.getAll() == null || metricsRepository.getAll().size() == 0) {
            writeResponse(response, "No metrics available");
            return;
        }

        metricsRepository.getAll().forEach(sm -> {
            String sanitizedMetricKey = validateAndSanitizeUtf8(sm.getMetricKey());
            String sanitizedShortKey = validateAndSanitizeUtf8(sm.getShortKey());

            String scalarEntry = String.format(
                    HELP_TEXT + TYPE_TEXT + METRIC_TEXT,
                    sanitizedMetricKey, sanitizedMetricKey, sanitizedMetricKey, sanitizedShortKey, sm.getCount());

            logger.info("Processed sanitized metric: Short Key: {}, Metric Key: {}", sanitizedShortKey, sanitizedMetricKey);
            writeResponse(response, scalarEntry);
        });
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

    private void writeResponse(HttpServletResponse response, String message) {
        try {
            response.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));
            response.flushBuffer();
        } catch (IOException e) {
            logger.warn("Buffer error: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There was a problem writing the response.");
        }
    }
}
