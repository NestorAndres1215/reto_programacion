package api_gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GatewayErrorAttributes {

    public Map<String, Object> build(
            HttpStatus status,
            String code,
            String message,
            String traceId) {

        Map<String, Object> attributes = new LinkedHashMap<>();

        attributes.put("timestamp", Instant.now().toString());
        attributes.put("status", status.value());
        attributes.put("code", code);
        attributes.put("message", message);
        attributes.put("traceId", traceId);

        return attributes;
    }
}