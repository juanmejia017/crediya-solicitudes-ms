package co.com.crediya.usecase.autenticacion;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.exceptions.NoAutenticadoException;
import co.com.crediya.model.rol.Rol;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AutenticacionUseCaseTest {

    private static final UsuarioAutenticado CLIENTE =
            new UsuarioAutenticado(7L, "ana@correo.com", "1020304050", Rol.CLIENTE);

    private final AutenticacionUseCase useCase =
            new AutenticacionUseCase(token -> "valido".equals(token) ? Optional.of(CLIENTE) : Optional.empty());

    @Test
    void devuelveElUsuarioDelToken() {
        assertEquals(CLIENTE, useCase.validarToken("Bearer valido"));
    }

    @Test
    void rechazaHeaderAusenteOSinBearer() {
        assertThrows(NoAutenticadoException.class, () -> useCase.validarToken(null));
        assertThrows(NoAutenticadoException.class, () -> useCase.validarToken("valido"));
    }

    @Test
    void rechazaTokenInvalido() {
        assertThrows(NoAutenticadoException.class, () -> useCase.validarToken("Bearer otro"));
    }
}
