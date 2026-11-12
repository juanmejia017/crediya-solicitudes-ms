package co.com.crediya.api.dto;

import co.com.crediya.model.solicitud.Solicitud;

import java.math.BigDecimal;

public record RegistrarSolicitudRequest(
        BigDecimal monto,
        Integer plazo,
        String email,
        String documentoIdentidad,
        Long idTipoPrestamo
) {
    public Solicitud toDomain() {
        return Solicitud.builder()
                .monto(monto)
                .plazo(plazo)
                .email(email)
                .documentoIdentidad(documentoIdentidad)
                .idTipoPrestamo(idTipoPrestamo)
                .build();
    }
}
