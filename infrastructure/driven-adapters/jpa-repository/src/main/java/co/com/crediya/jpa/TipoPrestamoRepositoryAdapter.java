package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.jpa.repository.TipoPrestamoJpaRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TipoPrestamoRepositoryAdapter implements TipoPrestamoRepository {

    private final TipoPrestamoJpaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoPrestamo> findByIdTipoPrestamo(Long idTipoPrestamo) {
        Optional<TipoPrestamo> tipo = repository.findById(idTipoPrestamo).map(TipoPrestamoRepositoryAdapter::toDomain);
        if (tipo.isEmpty()) {
            log.warn("Tipo de préstamo {} no encontrado", idTipoPrestamo);
        }
        return tipo;
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
