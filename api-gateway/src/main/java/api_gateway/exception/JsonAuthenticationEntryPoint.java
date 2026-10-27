
package api_gateway.exception;

import api_gateway.exception.dto.GatewayErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class JsonAuthenticationEntryPoint
        implements ServerAuthenticationEntryPoint {

    private final GatewayErrorAttributes errorAttributes;
    private final tools.jackson.databind.ObjectMapper objectMapper;

    public JsonAuthenticationEntryPoint(
            GatewayErrorAttributes errorAttributes,
            tools.jackson.databind.ObjectMapper objectMapper) {
        this.errorAttributes = errorAttributes;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> commence(
            ServerWebExchange exchange,
            AuthenticationException exception) {

        var response = exchange.getResponse();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String traceId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-Trace-Id");

        if (traceId == null || traceId.isBlank()) {
            traceId = "no-disponible";
        }

        GatewayErrorResponse body = errorAttributes.build(
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                "Se requiere autenticación para acceder a este recurso.",
                traceId
        );

        try {
            byte[] bytes = objectMapper.writeValueAsString(body)
                    .getBytes(StandardCharsets.UTF_8);

            var buffer = response.bufferFactory().wrap(bytes);

            return response.writeWith(Mono.just(buffer));
        } catch (Exception exceptionWritingResponse) {
            return Mono.error(exceptionWritingResponse);
        }
    }
}
