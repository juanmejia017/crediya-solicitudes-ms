package co.com.crediya.jpa.helper;

import co.com.crediya.jpa.SolicitudRepositoryAdapter;
import co.com.crediya.jpa.entity.SolicitudEntity;
import co.com.crediya.jpa.repository.SolicitudJpaRepository;
import co.com.crediya.model.solicitud.Solicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Pruebas de las operaciones genéricas de AdapterOperations, usando el adaptador de solicitudes.
 */
class AdapterOperationsTest {

    @Mock
    private SolicitudJpaRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private SolicitudRepositoryAdapter adapter;

    private Solicitud solicitud;
    private SolicitudEntity entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new SolicitudRepositoryAdapter(repository, objectMapper);

        solicitud = Solicitud.builder()
                .monto(new BigDecimal("5000000")).plazo(24).email("juan@correo.com")
                .documentoIdentidad("1020304050").idEstado(1L).idTipoPrestamo(1L)
                .build();

        entity = SolicitudEntity.builder()
                .idSolicitud(1L)
                .monto(new BigDecimal("5000000")).plazo(24).email("juan@correo.com")
                .documentoIdentidad("1020304050").idEstado(1L).idTipoPrestamo(1L)
                .build();
    }

    @Test
    void testSaveAllEntities() {
        when(repository.<SolicitudEntity>saveAll(ArgumentMatchers.<SolicitudEntity>anyList()))
                .thenReturn(List.of(entity, entity));

        List<Solicitud> result = adapter.saveAllEntities(List.of(solicitud, solicitud));

        assertEquals(2, result.size());
    }

    @Test
    void testFindById() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Solicitud result = adapter.findById(1L);

        assertEquals(1L, result.getIdSolicitud());
    }

    @Test
    void testFindAll() {
        when(repository.findAll()).thenReturn(List.of(entity, entity));

        List<Solicitud> result = adapter.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void testFindByExample() {
        when(repository.findAll(ArgumentMatchers.<Example<SolicitudEntity>>any())).thenReturn(List.of(entity));

        List<Solicitud> result = adapter.findByExample(solicitud);

        assertEquals(1, result.size());
    }
}
