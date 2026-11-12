package co.com.crediya.r2dbc;

import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.r2dbc.entity.SolicitudEntity;
import co.com.crediya.r2dbc.repository.SolicitudReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SolicitudRepositoryAdapterTest {

    private SolicitudReactiveRepository repository;
    private SolicitudRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(SolicitudReactiveRepository.class);
        adapter = new SolicitudRepositoryAdapter(repository, mock(ObjectMapper.class));
    }

    @Test
    void guardaLaSolicitudYMapeaElIdGenerado() {
        Solicitud solicitud = Solicitud.builder()
                .monto(new BigDecimal("5000000")).plazo(24).email("juan@correo.com")
                .documentoIdentidad("1020304050").idEstado(1L).idTipoPrestamo(2L)
                .build();
        when(repository.save(any(SolicitudEntity.class))).thenAnswer(invocation -> {
            SolicitudEntity entity = invocation.getArgument(0);
            entity.setIdSolicitud(10L);
            return Mono.just(entity);
        });

        StepVerifier.create(adapter.save(solicitud))
                .assertNext(guardada -> {
                    assertEquals(10L, guardada.getIdSolicitud());
                    assertEquals(1L, guardada.getIdEstado());
                    assertEquals(2L, guardada.getIdTipoPrestamo());
                    assertEquals("1020304050", guardada.getDocumentoIdentidad());
                })
                .verifyComplete();

        ArgumentCaptor<SolicitudEntity> captor = ArgumentCaptor.forClass(SolicitudEntity.class);
        verify(repository).save(captor.capture());
        assertNull(captor.getValue().getIdSolicitud());
        assertEquals(24, captor.getValue().getPlazo());
    }
}
