package co.com.crediya.jpa.mapper;

import co.com.crediya.jpa.entity.SolicitudEntity;
import co.com.crediya.model.solicitud.Solicitud;

/**
 * Convierte entre el modelo del dominio (Solicitud) y la entidad de la base de datos (SolicitudEntity).
 */
public final class SolicitudEntityMapper {

    private SolicitudEntityMapper() {
    }

    public static SolicitudEntity aEntidad(Solicitud solicitud) {
        return SolicitudEntity.builder()
                .idSolicitud(solicitud.getIdSolicitud())
                .monto(solicitud.getMonto())
                .plazo(solicitud.getPlazo())
                .email(solicitud.getEmail())
                .documentoIdentidad(solicitud.getDocumentoIdentidad())
                .idEstado(solicitud.getIdEstado())
                .idTipoPrestamo(solicitud.getIdTipoPrestamo())
                .build();
    }

    public static Solicitud aDominio(SolicitudEntity entity) {
        return Solicitud.builder()
                .idSolicitud(entity.getIdSolicitud())
                .monto(entity.getMonto())
                .plazo(entity.getPlazo())
                .email(entity.getEmail())
                .documentoIdentidad(entity.getDocumentoIdentidad())
                .idEstado(entity.getIdEstado())
                .idTipoPrestamo(entity.getIdTipoPrestamo())
                .build();
    }
}
