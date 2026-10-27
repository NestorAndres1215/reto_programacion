package api_gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TraceIdFilter implements GlobalFilter, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(TraceIdFilter.class);

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String traceId = exchange.getRequest()
                .getHeaders()
                .getFirst(TraceIdWebFilter.TRACE_ID_HEADER);

        if (traceId == null || traceId.isBlank()) {
            traceId = exchange.getAttribute(
                    TraceIdWebFilter.TRACE_ID_CONTEXT_KEY
            );
        }

        if (traceId == null || traceId.isBlank()) {
            return chain.filter(exchange);
        }

        String finalTraceId = traceId;

        ServerHttpRequest request = exchange.getRequest()
                .mutate()
                .headers(headers ->
                        headers.set(
                                TraceIdWebFilter.TRACE_ID_HEADER,
                                finalTraceId
                        )
                )
                .build();

        ServerWebExchange updatedExchange = exchange.mutate()
                .request(request)
                .build();

        log.info(
                "Gateway request traceId={} method={} path={}",
                finalTraceId,
                request.getMethod(),
                request.getURI().getPath()
        );

        return chain.filter(updatedExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}