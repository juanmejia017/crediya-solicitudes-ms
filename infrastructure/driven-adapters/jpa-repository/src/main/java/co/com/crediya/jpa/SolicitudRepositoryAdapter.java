package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.SolicitudEntity;
import co.com.crediya.jpa.repository.SolicitudJpaRepository;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SolicitudRepositoryAdapter implements SolicitudRepository {

    private final SolicitudJpaRepository repository;

    @Override
    @Transactional
    public Solicitud save(Solicitud solicitud) {
        log.info("Persistiendo nueva solicitud (tipo={})", solicitud.getIdTipoPrestamo());
        SolicitudEntity entidad = toData(solicitud);
        Solicitud guardada = toDomain(repository.save(entidad));
        log.info("Solicitud persistida con id {}", guardada.getIdSolicitud());
        return guardada;
    }

    private static SolicitudEntity toData(Solicitud solicitud) {
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

    private static Solicitud toDomain(SolicitudEntity entity) {
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
