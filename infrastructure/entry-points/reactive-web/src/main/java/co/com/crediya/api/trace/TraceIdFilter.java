package co.com.crediya.api.trace;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

/**
 * Asigna un identificador de traza a cada petición (o reutiliza el que envía el cliente en X-Trace-Id),
 * lo devuelve en la respuesta y registra inicio, fin, estado HTTP y duración de cada petición.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter implements WebFilter {

    public static final String TRACE_HEADER = "X-Trace-Id";
    public static final String TRACE_ATTRIBUTE = "traceId";
    private static final Pattern TRACE_VALIDO = Pattern.compile("^[A-Za-z0-9-]{8,64}$");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = resolverTraceId(exchange.getRequest().getHeaders().getFirst(TRACE_HEADER));
        exchange.getAttributes().put(TRACE_ATTRIBUTE, traceId);
        exchange.getResponse().getHeaders().set(TRACE_HEADER, traceId);

        String metodo = exchange.getRequest().getMethod().name();
        String ruta = exchange.getRequest().getPath().value();
        long inicio = System.nanoTime();
        log.info("[{}] --> {} {}", traceId, metodo, ruta);

        return chain.filter(exchange)
                .doFinally(signal -> log.info("[{}] <-- {} {} status={} ({} ms)", traceId, metodo, ruta,
                        exchange.getResponse().getStatusCode(), (System.nanoTime() - inicio) / 1_000_000));
    }

    public static String traceIdDe(ServerWebExchange exchange) {
        Object traceId = exchange.getAttribute(TRACE_ATTRIBUTE);
        return traceId != null ? traceId.toString() : "sin-traza";
    }

    private String resolverTraceId(String recibido) {
        if (recibido != null && TRACE_VALIDO.matcher(recibido).matches()) {
            return recibido;
        }
        // ThreadLocalRandom en lugar de UUID.randomUUID() para no bloquear el event loop (SecureRandom)
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong()).toString();
    }
}
