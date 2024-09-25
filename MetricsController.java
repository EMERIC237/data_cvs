import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
            // ScalarMetric class handles sanitization internally
            String metricKey = sm.getMetricKey();
            String shortKey = sm.getShortKey();

            String scalarEntry = String.format(
                    HELP_TEXT + TYPE_TEXT + METRIC_TEXT,
                    metricKey, metricKey, metricKey, shortKey, sm.getCount());

            logger.info("Processed metric: Short Key: {}, Metric Key: {}", shortKey, metricKey);
            writeResponse(response, scalarEntry);
        });
    }

    private void writeResponse(HttpServletResponse response, String message) {
        try {
            response.getOutputStream().write(message.getBytes());
            response.flushBuffer();
        } catch (IOException e) {
            logger.warn("Buffer error: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There was a problem writing the response.");
        }
    }
}
