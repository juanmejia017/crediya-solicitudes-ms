package co.com.crediya.jpa;

import co.com.crediya.jpa.mapper.SolicitudEntityMapper;
import co.com.crediya.jpa.repository.SolicitudJpaRepository;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistencia de solicitudes: guarda en la tabla solicitud.
 * La conversión entre Solicitud y SolicitudEntity la hace SolicitudEntityMapper.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SolicitudRepositoryAdapter implements SolicitudRepository {

    private final SolicitudJpaRepository repository;

    @Override
    @Transactional
    public Solicitud guardar(Solicitud solicitud) {
        log.info("Persistiendo nueva solicitud (tipo={})", solicitud.getIdTipoPrestamo());
        Solicitud guardada = SolicitudEntityMapper.aDominio(repository.save(SolicitudEntityMapper.aEntidad(solicitud)));
        log.info("Solicitud persistida con id {}", guardada.getIdSolicitud());
        return guardada;
    }
}
