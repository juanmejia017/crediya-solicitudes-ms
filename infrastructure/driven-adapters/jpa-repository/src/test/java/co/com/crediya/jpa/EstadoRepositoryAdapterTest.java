package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.EstadoEntity;
import co.com.crediya.jpa.repository.EstadoJpaRepository;
import co.com.crediya.model.estado.Estado;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EstadoRepositoryAdapterTest {

    @Test
    void encuentraEstadoPorNombre() {
        EstadoJpaRepository repository = mock(EstadoJpaRepository.class);
        EstadoRepositoryAdapter adapter = new EstadoRepositoryAdapter(repository);
        when(repository.findByNombre(Estado.PENDIENTE_REVISION)).thenReturn(Optional.of(
                EstadoEntity.builder().idEstado(1L).nombre(Estado.PENDIENTE_REVISION).descripcion("Inicial").build()));

        Optional<Estado> estado = adapter.buscarPorNombre(Estado.PENDIENTE_REVISION);

        assertTrue(estado.isPresent());
        assertEquals(1L, estado.get().getIdEstado());
    }
}
