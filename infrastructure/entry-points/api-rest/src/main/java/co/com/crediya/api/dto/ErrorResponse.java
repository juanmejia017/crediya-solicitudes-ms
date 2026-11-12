package co.com.crediya.api.dto;

import co.com.crediya.api.trace.TraceIdFilter;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String codigo,
        String mensaje,
        List<String> detalles,
        String traceId
) {
    public static ErrorResponse of(int status, String codigo, String mensaje, List<String> detalles) {
        return new ErrorResponse(LocalDateTime.now(), status, codigo, mensaje, detalles, MDC.get(TraceIdFilter.TRACE_ID));
    }
}
