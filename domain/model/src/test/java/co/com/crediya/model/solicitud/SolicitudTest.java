package co.com.crediya.model.solicitud;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SolicitudTest {

    @Test
    void normalizaEmailYDocumentoYQuitaDatosDelSistema() {
        Solicitud solicitud = Solicitud.builder()
                .idSolicitud(50L).idEstado(99L).estado("Aprobada").tipoPrestamo("Vivienda")
                .monto(new BigDecimal("5000000")).plazo(24)
                .email("  Juan@Correo.COM ").documentoIdentidad(" 1020304050 ").idTipoPrestamo(1L)
                .build();

        Solicitud normalizada = solicitud.normalizada();

        assertNull(normalizada.getIdSolicitud());
        assertNull(normalizada.getIdEstado());
        assertNull(normalizada.getEstado());
        assertNull(normalizada.getTipoPrestamo());
        assertEquals("juan@correo.com", normalizada.getEmail());
        assertEquals("1020304050", normalizada.getDocumentoIdentidad());
    }

    @Test
    void asignaEstadoYDescripciones() {
        Estado estado = Estado.builder().idEstado(1L).nombre(Estado.PENDIENTE_REVISION).build();
        TipoPrestamo tipo = TipoPrestamo.builder().idTipoPrestamo(1L).nombre("Libre inversión").build();
        Solicitud solicitud = Solicitud.builder().idTipoPrestamo(1L).build();

        Solicitud conEstado = solicitud.conEstado(estado);
        Solicitud conDescripciones = conEstado.conDescripciones(estado, tipo);

        assertEquals(1L, conEstado.getIdEstado());
        assertNull(conEstado.getEstado());
        assertEquals(Estado.PENDIENTE_REVISION, conDescripciones.getEstado());
        assertEquals("Libre inversión", conDescripciones.getTipoPrestamo());
    }
}
