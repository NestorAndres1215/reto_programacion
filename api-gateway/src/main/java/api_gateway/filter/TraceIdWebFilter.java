package api_gateway.filter;


import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.regex.Pattern;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdWebFilter implements WebFilter {

    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String TRACE_ID_CONTEXT_KEY = "traceId";

    private static final Pattern VALID_TRACE_ID =
            Pattern.compile("[A-Za-z0-9._-]{1,100}");

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            WebFilterChain chain) {

        String incomingTraceId = exchange.getRequest()
                .getHeaders()
                .getFirst(TRACE_ID_HEADER);

        String traceId = resolveTraceId(incomingTraceId);

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .headers(headers -> headers.set(TRACE_ID_HEADER, traceId))
                .build();

        ServerWebExchange updatedExchange = exchange.mutate()
                .request(request)
                .build();

        updatedExchange.getResponse()
                .getHeaders()
                .set(TRACE_ID_HEADER, traceId);

        updatedExchange.getAttributes().put(TRACE_ID_CONTEXT_KEY, traceId);

        return chain.filter(updatedExchange)
                .contextWrite(context -> context.put(TRACE_ID_CONTEXT_KEY, traceId));
    }

    private String resolveTraceId(String incomingTraceId) {
        if (incomingTraceId != null && VALID_TRACE_ID.matcher(incomingTraceId).matches()) {
            return incomingTraceId;
        }

        return UUID.randomUUID().toString();
    }
}