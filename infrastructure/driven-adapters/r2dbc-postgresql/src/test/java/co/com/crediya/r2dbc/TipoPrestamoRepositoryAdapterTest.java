package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entity.TipoPrestamoEntity;
import co.com.crediya.r2dbc.repository.TipoPrestamoReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TipoPrestamoRepositoryAdapterTest {

    private TipoPrestamoReactiveRepository repository;
    private TipoPrestamoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(TipoPrestamoReactiveRepository.class);
        adapter = new TipoPrestamoRepositoryAdapter(repository, mock(ObjectMapper.class));
    }

    @Test
    void encuentraTipoDePrestamoPorId() {
        when(repository.findById(1L)).thenReturn(Mono.just(TipoPrestamoEntity.builder()
                .idTipoPrestamo(1L).nombre("Libre inversión")
                .montoMinimo(new BigDecimal("1000000")).montoMaximo(new BigDecimal("50000000"))
                .tasaInteres(new BigDecimal("1.80")).validacionAutomatica(true)
                .build()));

        StepVerifier.create(adapter.findById(1L))
                .assertNext(tipo -> {
                    assertEquals("Libre inversión", tipo.getNombre());
                    assertEquals(new BigDecimal("50000000"), tipo.getMontoMaximo());
                })
                .verifyComplete();
    }

    @Test
    void retornaVacioSiElTipoNoExiste() {
        when(repository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(99L))
                .verifyComplete();
    }
}
