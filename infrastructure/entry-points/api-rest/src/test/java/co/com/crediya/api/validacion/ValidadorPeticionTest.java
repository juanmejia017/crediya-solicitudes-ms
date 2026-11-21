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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ValidadorPeticionTest {

    private static final UsuarioAutenticado USUARIO = new UsuarioAutenticado(7L, "ana@correo.com", "1037654321", Rol.CLIENTE);

    private final AutenticacionUseCase autenticacionUseCase = mock(AutenticacionUseCase.class);
    private final ValidadorPeticion validador = new ValidadorPeticion(autenticacionUseCase);

    @Test
    void sacaElTokenDelHeaderBearerYDevuelveElSolicitante() {
        when(autenticacionUseCase.validarToken("abc.def.ghi")).thenReturn(USUARIO);

        assertEquals(USUARIO, validador.validarPeticionAutenticada("Bearer abc.def.ghi", new Object()));
    }

    @Test
    void sinHeaderOSinBearerNoLlamaAlCasoDeUso() {
        assertThrows(NoAutenticadoException.class, () -> validador.validarPeticionAutenticada(null, new Object()));
        assertThrows(NoAutenticadoException.class, () -> validador.validarPeticionAutenticada("abc.def.ghi", new Object()));
        verify(autenticacionUseCase, never()).validarToken(any());
    }

    @Test
    void primeroElTokenYDespuesElCuerpo() {
        when(autenticacionUseCase.validarToken("abc.def.ghi")).thenReturn(USUARIO);

        assertThrows(DatosInvalidosException.class, () -> validador.validarPeticionAutenticada("Bearer abc.def.ghi", null));
        assertThrows(NoAutenticadoException.class, () -> validador.validarPeticionAutenticada(null, null));
    }

    @Test
    void validaQueElCuerpoNoVengaVacio() {
        assertThrows(DatosInvalidosException.class, () -> validador.validarCuerpo(null));
        assertDoesNotThrow(() -> validador.validarCuerpo("algo"));
    }
}
