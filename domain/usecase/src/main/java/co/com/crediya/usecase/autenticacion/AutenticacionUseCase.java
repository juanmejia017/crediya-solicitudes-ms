package co.com.crediya.usecase.autenticacion;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.auth.gateways.TokenGateway;
import co.com.crediya.model.exceptions.NoAutenticadoException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AutenticacionUseCase {

    private static final String PREFIJO_BEARER = "Bearer ";

    private final TokenGateway tokenGateway;

    /**
     * Recibe el header Authorization ("Bearer <token>") y devuelve quién hizo la petición.
     */
    public UsuarioAutenticado validarToken(String authorization) {
        if (authorization == null || !authorization.startsWith(PREFIJO_BEARER)) {
            throw new NoAutenticadoException("Debes iniciar sesión y enviar el token en el header Authorization: Bearer <token>");
        }
        String token = authorization.substring(PREFIJO_BEARER.length()).trim();
        return tokenGateway.validar(token)
                .orElseThrow(() -> new NoAutenticadoException("El token es inválido o ya expiró. Inicia sesión de nuevo"));
    }
}
