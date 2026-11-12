package co.com.crediya.r2dbc;

import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.r2dbc.entity.TipoPrestamoEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.repository.TipoPrestamoReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class TipoPrestamoRepositoryAdapter extends ReactiveAdapterOperations<
        TipoPrestamo,
        TipoPrestamoEntity,
        Long,
        TipoPrestamoReactiveRepository
        > implements TipoPrestamoRepository {

    public TipoPrestamoRepositoryAdapter(TipoPrestamoReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, TipoPrestamoRepositoryAdapter::toDomain);
    }

    @Override
    public Mono<TipoPrestamo> findById(Long idTipoPrestamo) {
        return super.findById(idTipoPrestamo)
                .doOnNext(tipo -> log.debug("Tipo de préstamo {} encontrado", idTipoPrestamo))
                .doOnSuccess(tipo -> {
                    if (tipo == null) {
                        log.warn("Tipo de préstamo {} no encontrado", idTipoPrestamo);
                    }
                });
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

    static TipoPrestamo toDomain(TipoPrestamoEntity entity) {
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
