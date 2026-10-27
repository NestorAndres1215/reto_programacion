
package api_gateway.exception;

import api_gateway.exception.dto.GatewayErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class GatewayErrorAttributes {

    public GatewayErrorResponse build(
            HttpStatus status,
            String code,
            String message,
            String traceId) {

        return new GatewayErrorResponse(
                Instant.now().toString(),
                status.value(),
                code,
                message,
                traceId
        );
    }
}
