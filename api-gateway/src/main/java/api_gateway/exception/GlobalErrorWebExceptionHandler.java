
package api_gateway.exception;

import api_gateway.exception.dto.GatewayErrorResponse;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

@Component
@Order(-2)
public class GlobalErrorWebExceptionHandler
        implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;
    private final GatewayErrorAttributes errorAttributes;

    public GlobalErrorWebExceptionHandler(
            ObjectMapper objectMapper,
            GatewayErrorAttributes errorAttributes) {
        this.objectMapper = objectMapper;
        this.errorAttributes = errorAttributes;
    }

    @Override
    public Mono<Void> handle(
            ServerWebExchange exchange,
            Throwable ex) {

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        var response = exchange.getResponse();

        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String traceId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Trace-Id");

        if (traceId == null || traceId.isBlank()) {
            traceId = "no-disponible";
        }

        GatewayErrorResponse body = errorAttributes.build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Ha ocurrido un error en el API Gateway",
                traceId
        );

        try {
            byte[] bytes = objectMapper.writeValueAsString(body)
                    .getBytes(StandardCharsets.UTF_8);

            DataBuffer buffer = response.bufferFactory().wrap(bytes);

            return response.writeWith(Mono.just(buffer));
        } catch (Exception serializationException) {
            return Mono.error(serializationException);
        }
    }
}
