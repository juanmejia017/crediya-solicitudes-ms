package co.com.crediya.r2dbc;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.r2dbc.entity.EstadoEntity;
import co.com.crediya.r2dbc.repository.EstadoReactiveRepository;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EstadoRepositoryAdapterTest {

    @Test
    void encuentraEstadoPorNombre() {
        EstadoReactiveRepository repository = mock(EstadoReactiveRepository.class);
        EstadoRepositoryAdapter adapter = new EstadoRepositoryAdapter(repository, mock(ObjectMapper.class));
        when(repository.findByNombre(Estado.PENDIENTE_REVISION)).thenReturn(Mono.just(
                EstadoEntity.builder().idEstado(1L).nombre(Estado.PENDIENTE_REVISION).descripcion("Inicial").build()));

        StepVerifier.create(adapter.findByNombre(Estado.PENDIENTE_REVISION))
                .assertNext(estado -> {
                    assertEquals(1L, estado.getIdEstado());
                    assertEquals(Estado.PENDIENTE_REVISION, estado.getNombre());
                })
                .verifyComplete();
    }
}
