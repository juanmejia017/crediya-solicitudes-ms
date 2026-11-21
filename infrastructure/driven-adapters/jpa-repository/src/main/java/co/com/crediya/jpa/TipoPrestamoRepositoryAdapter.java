package co.com.crediya.jpa;

import co.com.crediya.jpa.mapper.TipoPrestamoEntityMapper;
import co.com.crediya.jpa.repository.TipoPrestamoJpaRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Consulta de tipos de préstamo en la tabla tipo_prestamo.
 * La conversión a TipoPrestamo la hace TipoPrestamoEntityMapper.
 */
@Repository
@RequiredArgsConstructor
public class TipoPrestamoRepositoryAdapter implements TipoPrestamoRepository {

    private final TipoPrestamoJpaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<TipoPrestamo> buscarPorId(Long idTipoPrestamo) {
        return repository.findById(idTipoPrestamo).map(TipoPrestamoEntityMapper::aDominio);
    }
}
