package co.com.crediya.model.auth.gateways;

import co.com.crediya.model.auth.UsuarioAutenticado;

import java.util.Optional;

/**
 * Valida los tokens que emite crediya-autenticacion-ms.
 */
public interface TokenGateway {
    Optional<UsuarioAutenticado> validar(String token);
}
