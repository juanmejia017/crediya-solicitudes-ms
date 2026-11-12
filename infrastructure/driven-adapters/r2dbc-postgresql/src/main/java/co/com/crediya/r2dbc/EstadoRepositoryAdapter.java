package co.com.crediya.r2dbc;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.r2dbc.entity.EstadoEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.repository.EstadoReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class EstadoRepositoryAdapter extends ReactiveAdapterOperations<
        Estado,
        EstadoEntity,
        Long,
        EstadoReactiveRepository
        > implements EstadoRepository {

    public EstadoRepositoryAdapter(EstadoReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, EstadoRepositoryAdapter::toDomain);
    }

    @Override
    public Mono<Estado> findByNombre(String nombre) {
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

    static Estado toDomain(EstadoEntity entity) {
        return Estado.builder()
                .idEstado(entity.getIdEstado())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }
}
