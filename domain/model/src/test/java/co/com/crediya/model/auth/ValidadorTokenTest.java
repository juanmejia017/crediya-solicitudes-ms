package co.com.crediya.model.auth;

import co.com.crediya.model.exceptions.NoAutenticadoException;
import co.com.crediya.model.rol.Rol;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidadorTokenTest {

    private static final UsuarioAutenticado CLIENTE = new UsuarioAutenticado(7L, "ana@correo.com", "1037654321", Rol.CLIENTE);

    @Test
    void elTokenDebeVenir() {
        assertThrows(NoAutenticadoException.class, () -> ValidadorToken.validarPresente(null));
        assertThrows(NoAutenticadoException.class, () -> ValidadorToken.validarPresente(" "));
        assertDoesNotThrow(() -> ValidadorToken.validarPresente("abc"));
    }

    @Test
    void elTokenDebeSerReconocido() {
        assertEquals(CLIENTE, ValidadorToken.validarVigente(Optional.of(CLIENTE)));
        assertThrows(NoAutenticadoException.class, () -> ValidadorToken.validarVigente(Optional.empty()));
    }
}
