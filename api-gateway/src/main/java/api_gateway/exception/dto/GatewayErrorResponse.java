
package api_gateway.exception.dto;

public record GatewayErrorResponse(
        String timestamp,
        int status,
        String code,
        String message,
        String traceId
) {
}
