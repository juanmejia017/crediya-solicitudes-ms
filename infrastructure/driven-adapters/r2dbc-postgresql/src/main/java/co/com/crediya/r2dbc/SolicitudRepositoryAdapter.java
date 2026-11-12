package co.com.crediya.r2dbc;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.r2dbc.entity.SolicitudEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.repository.SolicitudReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class SolicitudRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud,
        SolicitudEntity,
        Long,
        SolicitudReactiveRepository
        > implements SolicitudRepository {

    public SolicitudRepositoryAdapter(SolicitudReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, SolicitudRepositoryAdapter::toDomain);
    }

    @Override
    public Mono<Solicitud> save(Solicitud solicitud) {
        return super.save(solicitud)
                .doOnSubscribe(s -> log.debug("Guardando solicitud en base de datos"))
                .doOnNext(guardada -> log.info("Solicitud persistida con id {}", guardada.getIdSolicitud()))
                .doOnError(error -> log.error("Error al persistir la solicitud: {}", error.getMessage()));
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

    static Solicitud toDomain(SolicitudEntity entity) {
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
