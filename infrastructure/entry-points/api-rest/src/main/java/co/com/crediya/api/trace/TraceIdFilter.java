package co.com.crediya.api.trace;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Asigna un identificador de traza a cada petición (o reutiliza el que envía el cliente en X-Trace-Id),
 * lo deja en el MDC para que salga en todos los logs, lo devuelve en la respuesta y registra
 * inicio, fin, estado HTTP y duración de cada petición.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class TraceIdFilter extends OncePerRequestFilter {

    public static final String TRACE_HEADER = "X-Trace-Id";
    public static final String TRACE_ID = "traceId";
    private static final Pattern TRACE_VALIDO = Pattern.compile("^[A-Za-z0-9-]{8,64}$");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String traceId = resolverTraceId(request.getHeader(TRACE_HEADER));
        MDC.put(TRACE_ID, traceId);
        response.setHeader(TRACE_HEADER, traceId);

        long inicio = System.currentTimeMillis();
        log.info("--> {} {}", request.getMethod(), request.getRequestURI());
        try {
            chain.doFilter(request, response);
        } finally {
            log.info("<-- {} {} status={} ({} ms)", request.getMethod(), request.getRequestURI(),
                    response.getStatus(), System.currentTimeMillis() - inicio);
            MDC.remove(TRACE_ID);
        }
    }

    private String resolverTraceId(String recibido) {
        if (recibido != null && TRACE_VALIDO.matcher(recibido).matches()) {
            return recibido;
        }
        return UUID.randomUUID().toString();
    }
}
