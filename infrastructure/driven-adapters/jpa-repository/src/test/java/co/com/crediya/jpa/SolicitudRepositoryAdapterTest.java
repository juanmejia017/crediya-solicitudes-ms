package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.SolicitudEntity;
import co.com.crediya.jpa.repository.SolicitudJpaRepository;
import co.com.crediya.model.solicitud.Solicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.reactivecommons.utils.ObjectMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SolicitudRepositoryAdapterTest {

    private SolicitudJpaRepository repository;
    private SolicitudRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        repository = mock(SolicitudJpaRepository.class);
        adapter = new SolicitudRepositoryAdapter(repository, mock(ObjectMapper.class));
    }

    @Test
    void guardaLaSolicitudYMapeaElIdGenerado() {
        Solicitud solicitud = Solicitud.builder()
                .monto(new BigDecimal("5000000")).plazo(24).email("juan@correo.com")
                .documentoIdentidad("1020304050").idEstado(1L).idTipoPrestamo(2L)
                .build();
        when(repository.save(any(SolicitudEntity.class))).thenAnswer(invocation -> {
            SolicitudEntity enviada = invocation.getArgument(0);
            return SolicitudEntity.builder()
                    .idSolicitud(10L)
                    .monto(enviada.getMonto()).plazo(enviada.getPlazo()).email(enviada.getEmail())
                    .documentoIdentidad(enviada.getDocumentoIdentidad())
                    .idEstado(enviada.getIdEstado()).idTipoPrestamo(enviada.getIdTipoPrestamo())
                    .build();
        });

        Solicitud guardada = adapter.save(solicitud);

        assertEquals(10L, guardada.getIdSolicitud());
        assertEquals(1L, guardada.getIdEstado());
        assertEquals(2L, guardada.getIdTipoPrestamo());
        assertEquals("1020304050", guardada.getDocumentoIdentidad());

        ArgumentCaptor<SolicitudEntity> captor = ArgumentCaptor.forClass(SolicitudEntity.class);
        verify(repository).save(captor.capture());
        assertNull(captor.getValue().getIdSolicitud());
        assertEquals(24, captor.getValue().getPlazo());
    }
}
