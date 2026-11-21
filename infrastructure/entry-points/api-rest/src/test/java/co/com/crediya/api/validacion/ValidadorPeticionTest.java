package co.com.crediya.api.validacion;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.model.exceptions.NoAutenticadoException;
import co.com.crediya.model.rol.Rol;
import co.com.crediya.usecase.autenticacion.AutenticacionUseCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidadorPeticionTest {

    private static final UsuarioAutenticado CLIENTE = new UsuarioAutenticado(7L, "ana@correo.com", "1037654321", Rol.CLIENTE);

    private final AutenticacionUseCase autenticacionUseCase = mock(AutenticacionUseCase.class);
    private final ValidadorPeticion validador = new ValidadorPeticion(autenticacionUseCase);

    @Test
    void devuelveElSolicitanteSiElTokenYElCuerpoSonValidos() {
        when(autenticacionUseCase.validarToken("Bearer ok")).thenReturn(CLIENTE);

        assertEquals(CLIENTE, validador.validarPeticionAutenticada("Bearer ok", new Object()));
    }

    @Test
    void primeroValidaElToken() {
        when(autenticacionUseCase.validarToken(null)).thenThrow(new NoAutenticadoException("sin token"));

        assertThrows(NoAutenticadoException.class, () -> validador.validarPeticionAutenticada(null, null));
    }

    @Test
    void rechazaElCuerpoVacio() {
        when(autenticacionUseCase.validarToken("Bearer ok")).thenReturn(CLIENTE);

        assertThrows(DatosInvalidosException.class, () -> validador.validarPeticionAutenticada("Bearer ok", null));
        assertThrows(DatosInvalidosException.class, () -> validador.validarCuerpo(null));
        assertDoesNotThrow(() -> validador.validarCuerpo("algo"));
    }
}
