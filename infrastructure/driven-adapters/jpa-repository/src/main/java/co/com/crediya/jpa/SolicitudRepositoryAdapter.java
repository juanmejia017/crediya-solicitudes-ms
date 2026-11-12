package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.SolicitudEntity;
import co.com.crediya.jpa.helper.AdapterOperations;
import co.com.crediya.jpa.repository.SolicitudJpaRepository;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
public class SolicitudRepositoryAdapter extends AdapterOperations<Solicitud, SolicitudEntity, Long, SolicitudJpaRepository>
        implements SolicitudRepository {

    public SolicitudRepositoryAdapter(SolicitudJpaRepository repository, ObjectMapper mapper) {
        super(repository, mapper, SolicitudRepositoryAdapter::toDomain);
    }

    @Override
    @Transactional
    public Solicitud save(Solicitud solicitud) {
        log.info("Persistiendo nueva solicitud (tipo={})", solicitud.getIdTipoPrestamo());
        Solicitud guardada = super.save(solicitud);
        log.info("Solicitud persistida con id {}", guardada.getIdSolicitud());
        return guardada;
    }

    @Override
    protected SolicitudEntity toData(Solicitud solicitud) {
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
