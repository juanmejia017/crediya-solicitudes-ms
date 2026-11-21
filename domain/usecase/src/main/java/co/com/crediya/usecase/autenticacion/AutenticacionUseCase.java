package co.com.crediya.usecase.autenticacion;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.auth.gateways.TokenGateway;
import co.com.crediya.model.exceptions.NoAutenticadoException;
import lombok.RequiredArgsConstructor;

/**
 * Reconoce al usuario de un token emitido por crediya-autenticacion-ms.
 */
@RequiredArgsConstructor
public class AutenticacionUseCase {

    private final TokenGateway tokenGateway;

    /**
     * Devuelve quién es el dueño del token, o lanza NoAutenticadoException si el token no sirve.
     */
    public UsuarioAutenticado validarToken(String token) {
        if (token == null || token.isBlank()) {
            throw new NoAutenticadoException("Debes iniciar sesión y enviar el token en el header Authorization: Bearer <token>");
        }
        return tokenGateway.validar(token)
                .orElseThrow(() -> new NoAutenticadoException("El token es inválido o ya expiró. Inicia sesión de nuevo"));
    }
}
