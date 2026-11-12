package co.com.crediya.jpa.repository;

import co.com.crediya.jpa.entity.TipoPrestamoEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface TipoPrestamoJpaRepository extends CrudRepository<TipoPrestamoEntity, Long>, QueryByExampleExecutor<TipoPrestamoEntity> {

    boolean existsByNombre(String nombre);
}
