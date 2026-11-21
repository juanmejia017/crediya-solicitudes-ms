package co.com.crediya.model.auth;

import co.com.crediya.model.exceptions.NoAutenticadoException;

import java.util.Optional;

/**
 * Validaciones del token: que venga y que el generador de tokens lo reconozca.
 */
public final class ValidadorToken {

    private ValidadorToken() {
    }

    public static void validarPresente(String token) {
        if (token == null || token.isBlank()) {
            throw new NoAutenticadoException("Debes iniciar sesión y enviar el token en el header Authorization: Bearer <token>");
        }
    }

    public static UsuarioAutenticado validarVigente(Optional<UsuarioAutenticado> usuario) {
        return usuario.orElseThrow(
                () -> new NoAutenticadoException("El token es inválido o ya expiró. Inicia sesión de nuevo"));
    }
}
