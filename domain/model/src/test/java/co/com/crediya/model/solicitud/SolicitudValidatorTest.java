package co.com.crediya.model.solicitud;

import co.com.crediya.model.exceptions.DatosInvalidosException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SolicitudValidatorTest {

    private Solicitud.SolicitudBuilder solicitudValida() {
        return Solicitud.builder()
                .monto(new BigDecimal("5000000"))
                .plazo(24)
                .email("juan@correo.com")
                .documentoIdentidad("1020304050")
                .idTipoPrestamo(1L);
    }

    @Test
    void aceptaSolicitudValida() {
        assertDoesNotThrow(() -> SolicitudValidator.validar(solicitudValida().build()));
    }

    @Test
    void rechazaCuerpoNulo() {
        assertThrows(DatosInvalidosException.class, () -> SolicitudValidator.validar(null));
    }

    @Test
    void rechazaCamposObligatoriosVacios() {
        Solicitud vacia = Solicitud.builder().email(" ").build();

        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class, () -> SolicitudValidator.validar(vacia));

        assertEquals(5, ex.getErrores().size());
    }

    @Test
    void rechazaFormatosInvalidos() {
        Solicitud invalida = solicitudValida()
                .monto(new BigDecimal("-1"))
                .plazo(0)
                .email("correo-invalido")
                .documentoIdentidad("12")
                .idTipoPrestamo(-3L)
                .build();

        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class, () -> SolicitudValidator.validar(invalida));

        assertEquals(5, ex.getErrores().size());
    }

    @Test
    void rechazaMontoEnCero() {
        Solicitud solicitud = solicitudValida().monto(BigDecimal.ZERO).build();

        assertThrows(DatosInvalidosException.class, () -> SolicitudValidator.validar(solicitud));
    }

    @Test
    void aceptaPlazoEnLosLimites() {
        assertDoesNotThrow(() -> SolicitudValidator.validar(solicitudValida().plazo(1).build()));
        assertDoesNotThrow(() -> SolicitudValidator.validar(solicitudValida().plazo(360).build()));
    }

    @Test
    void rechazaPlazoMayorAlMaximo() {
        Solicitud solicitud = solicitudValida().plazo(361).build();

        assertThrows(DatosInvalidosException.class, () -> SolicitudValidator.validar(solicitud));
    }
}
