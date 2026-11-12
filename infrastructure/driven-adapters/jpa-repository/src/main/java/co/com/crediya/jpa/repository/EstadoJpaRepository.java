package co.com.crediya.jpa.repository;

import co.com.crediya.jpa.entity.EstadoEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.Optional;

public interface EstadoJpaRepository extends CrudRepository<EstadoEntity, Long>, QueryByExampleExecutor<EstadoEntity> {

    Optional<EstadoEntity> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
