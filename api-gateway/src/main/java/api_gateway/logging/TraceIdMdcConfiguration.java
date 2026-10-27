package api_gateway.logging;

import jakarta.annotation.PostConstruct;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import io.micrometer.context.ContextRegistry;
import reactor.core.publisher.Hooks;

@Configuration
public class TraceIdMdcConfiguration {

    @PostConstruct
    public void configureContextPropagation() {
        ContextRegistry.getInstance().registerThreadLocalAccessor(
                "traceId",
                () -> MDC.get("traceId"),
                value -> MDC.put("traceId", value),
                () -> MDC.remove("traceId")
        );

        Hooks.enableAutomaticContextPropagation();
    }
}