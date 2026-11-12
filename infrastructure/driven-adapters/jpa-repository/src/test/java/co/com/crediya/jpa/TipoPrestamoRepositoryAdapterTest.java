package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.jpa.repository.TipoPrestamoJpaRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TipoPrestamoRepositoryAdapterTest {

    private TipoPrestamoJpaRepository repository;
    private TipoPrestamoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(TipoPrestamoJpaRepository.class);
        adapter = new TipoPrestamoRepositoryAdapter(repository, mock(ObjectMapper.class));
    }

    @Test
    void encuentraTipoDePrestamoPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(TipoPrestamoEntity.builder()
                .idTipoPrestamo(1L).nombre("Libre inversión")
                .montoMinimo(new BigDecimal("1000000")).montoMaximo(new BigDecimal("50000000"))
                .tasaInteres(new BigDecimal("1.80")).validacionAutomatica(true)
                .build()));

        Optional<TipoPrestamo> tipo = adapter.findByIdTipoPrestamo(1L);

        assertTrue(tipo.isPresent());
        assertEquals("Libre inversión", tipo.get().getNombre());
        assertEquals(new BigDecimal("50000000"), tipo.get().getMontoMaximo());
    }

    @Test
    void retornaVacioSiElTipoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertTrue(adapter.findByIdTipoPrestamo(99L).isEmpty());
    }
}
