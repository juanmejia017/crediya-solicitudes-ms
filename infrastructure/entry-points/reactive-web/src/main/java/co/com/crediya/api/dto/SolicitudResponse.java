package co.com.crediya.api.dto;

import co.com.crediya.model.solicitud.Solicitud;

import java.math.BigDecimal;

public record SolicitudResponse(
        Long idSolicitud,
        BigDecimal monto,
        Integer plazo,
        String email,
        String documentoIdentidad,
        Long idTipoPrestamo,
        String tipoPrestamo,
        Long idEstado,
        String estado
) {
    public static SolicitudResponse fromDomain(Solicitud solicitud) {
        return new SolicitudResponse(
                solicitud.getIdSolicitud(),
                solicitud.getMonto(),
                solicitud.getPlazo(),
                solicitud.getEmail(),
                solicitud.getDocumentoIdentidad(),
                solicitud.getIdTipoPrestamo(),
                solicitud.getTipoPrestamo(),
                solicitud.getIdEstado(),
                solicitud.getEstado()
        );
    }
}
