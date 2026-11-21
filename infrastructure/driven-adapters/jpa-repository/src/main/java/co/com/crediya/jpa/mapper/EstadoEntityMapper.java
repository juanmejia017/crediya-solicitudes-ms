package co.com.crediya.jpa.mapper;

import co.com.crediya.jpa.entity.EstadoEntity;
import co.com.crediya.model.estado.Estado;

/**
 * Convierte la entidad de la tabla estados en el modelo del dominio (Estado).
 */
public final class EstadoEntityMapper {

    private EstadoEntityMapper() {
    }

    public static Estado aDominio(EstadoEntity entity) {
        return Estado.builder()
                .idEstado(entity.getIdEstado())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }
}
