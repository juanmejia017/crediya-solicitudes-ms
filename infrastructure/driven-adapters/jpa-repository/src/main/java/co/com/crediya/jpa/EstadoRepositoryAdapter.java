package co.com.crediya.jpa;

import co.com.crediya.jpa.mapper.EstadoEntityMapper;
import co.com.crediya.jpa.repository.EstadoJpaRepository;
import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Consulta de estados en la tabla estados.
 * La conversión a Estado la hace EstadoEntityMapper.
 */
@Repository
@RequiredArgsConstructor
public class EstadoRepositoryAdapter implements EstadoRepository {

    private final EstadoJpaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Estado> buscarPorNombre(String nombre) {
        return repository.findByNombre(nombre).map(EstadoEntityMapper::aDominio);
    }
}
