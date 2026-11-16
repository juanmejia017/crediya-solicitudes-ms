package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.EstadoEntity;
import co.com.crediya.jpa.repository.EstadoJpaRepository;
import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EstadoRepositoryAdapter implements EstadoRepository {

    private final EstadoJpaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Estado> findByNombre(String nombre) {
        return repository.findByNombre(nombre).map(EstadoRepositoryAdapter::toDomain);
    }

    private static Estado toDomain(EstadoEntity entity) {
        return Estado.builder()
                .idEstado(entity.getIdEstado())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }
}
