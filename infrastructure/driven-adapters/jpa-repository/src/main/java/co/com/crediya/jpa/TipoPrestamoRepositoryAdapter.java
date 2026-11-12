package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.jpa.helper.AdapterOperations;
import co.com.crediya.jpa.repository.TipoPrestamoJpaRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
public class TipoPrestamoRepositoryAdapter
        extends AdapterOperations<TipoPrestamo, TipoPrestamoEntity, Long, TipoPrestamoJpaRepository>
        implements TipoPrestamoRepository {

    public TipoPrestamoRepositoryAdapter(TipoPrestamoJpaRepository repository, ObjectMapper mapper) {
        super(repository, mapper, TipoPrestamoRepositoryAdapter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoPrestamo> findByIdTipoPrestamo(Long idTipoPrestamo) {
        Optional<TipoPrestamo> tipo = repository.findById(idTipoPrestamo).map(this::toEntity);
        if (tipo.isEmpty()) {
            log.warn("Tipo de préstamo {} no encontrado", idTipoPrestamo);
        }
        return tipo;
    }

    @Override
    protected TipoPrestamoEntity toData(TipoPrestamo tipo) {
        return TipoPrestamoEntity.builder()
                .idTipoPrestamo(tipo.getIdTipoPrestamo())
                .nombre(tipo.getNombre())
                .montoMinimo(tipo.getMontoMinimo())
                .montoMaximo(tipo.getMontoMaximo())
                .tasaInteres(tipo.getTasaInteres())
                .validacionAutomatica(tipo.getValidacionAutomatica())
                .build();
    }

    private static TipoPrestamo toDomain(TipoPrestamoEntity entity) {
        return TipoPrestamo.builder()
                .idTipoPrestamo(entity.getIdTipoPrestamo())
                .nombre(entity.getNombre())
                .montoMinimo(entity.getMontoMinimo())
                .montoMaximo(entity.getMontoMaximo())
                .tasaInteres(entity.getTasaInteres())
                .validacionAutomatica(entity.getValidacionAutomatica())
                .build();
    }
}
