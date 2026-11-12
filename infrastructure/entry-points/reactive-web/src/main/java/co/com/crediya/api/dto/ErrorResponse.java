package co.com.crediya.api.dto;

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
    public static ErrorResponse of(int status, String codigo, String mensaje, List<String> detalles, String traceId) {
        return new ErrorResponse(LocalDateTime.now(), status, codigo, mensaje, detalles, traceId);
    }
}
