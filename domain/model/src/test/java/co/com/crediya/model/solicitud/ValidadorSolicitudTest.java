package co.com.crediya.model.solicitud;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.exceptions.AccesoDenegadoException;
import co.com.crediya.model.rol.Rol;

import co.com.crediya.model.exceptions.DatosInvalidosException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidadorSolicitudTest {

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
        assertDoesNotThrow(() -> ValidadorSolicitud.validarDatos(solicitudValida().build()));
    }

    @Test
    void rechazaCuerpoNulo() {
        assertThrows(DatosInvalidosException.class, () -> ValidadorSolicitud.validarDatos(null));
    }

    @Test
    void rechazaCamposObligatoriosVacios() {
        Solicitud vacia = Solicitud.builder().email(" ").build();

        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class, () -> ValidadorSolicitud.validarDatos(vacia));

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

        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class, () -> ValidadorSolicitud.validarDatos(invalida));

        assertEquals(5, ex.getErrores().size());
    }

    @Test
    void rechazaMontoEnCero() {
        Solicitud solicitud = solicitudValida().monto(BigDecimal.ZERO).build();

        assertThrows(DatosInvalidosException.class, () -> ValidadorSolicitud.validarDatos(solicitud));
    }

    @Test
    void aceptaPlazoEnLosLimites() {
        assertDoesNotThrow(() -> ValidadorSolicitud.validarDatos(solicitudValida().plazo(1).build()));
        assertDoesNotThrow(() -> ValidadorSolicitud.validarDatos(solicitudValida().plazo(360).build()));
    }

    @Test
    void rechazaPlazoMayorAlMaximo() {
        Solicitud solicitud = solicitudValida().plazo(361).build();

        assertThrows(DatosInvalidosException.class, () -> ValidadorSolicitud.validarDatos(solicitud));
    }

    @Test
    void soloUnClientePuedeCrearSolicitudes() {
        assertDoesNotThrow(() -> ValidadorSolicitud.validarRolCliente(new UsuarioAutenticado(7L, "ana@correo.com", null, Rol.CLIENTE)));
        assertThrows(AccesoDenegadoException.class,
                () -> ValidadorSolicitud.validarRolCliente(new UsuarioAutenticado(1L, "admin@crediya.com", null, Rol.ADMIN)));
        assertThrows(AccesoDenegadoException.class, () -> ValidadorSolicitud.validarRolCliente(null));
    }

    @Test
    void elClienteDebeSerElTitular() {
        Solicitud solicitud = Solicitud.builder().email("ana@correo.com").documentoIdentidad("1037654321").build();

        assertDoesNotThrow(() -> ValidadorSolicitud.validarTitular(solicitud,
                new UsuarioAutenticado(7L, "ana@correo.com", "1037654321", Rol.CLIENTE)));
        assertThrows(AccesoDenegadoException.class, () -> ValidadorSolicitud.validarTitular(solicitud,
                new UsuarioAutenticado(8L, "pedro@correo.com", "1037654321", Rol.CLIENTE)));
    }
}
