package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.EstadoEntity;
import co.com.crediya.jpa.helper.AdapterOperations;
import co.com.crediya.jpa.repository.EstadoJpaRepository;
import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class EstadoRepositoryAdapter extends AdapterOperations<Estado, EstadoEntity, Long, EstadoJpaRepository>
        implements EstadoRepository {

    public EstadoRepositoryAdapter(EstadoJpaRepository repository, ObjectMapper mapper) {
        super(repository, mapper, EstadoRepositoryAdapter::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Estado> findByNombre(String nombre) {
        return repository.findByNombre(nombre).map(this::toEntity);
    }

    @Override
    protected EstadoEntity toData(Estado estado) {
        return EstadoEntity.builder()
                .idEstado(estado.getIdEstado())
                .nombre(estado.getNombre())
                .descripcion(estado.getDescripcion())
                .build();
    }

    private static Estado toDomain(EstadoEntity entity) {
        return Estado.builder()
                .idEstado(entity.getIdEstado())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }
}
