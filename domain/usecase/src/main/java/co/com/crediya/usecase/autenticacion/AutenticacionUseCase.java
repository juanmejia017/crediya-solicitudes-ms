package co.com.crediya.usecase.autenticacion;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.auth.ValidadorToken;
import co.com.crediya.model.auth.gateways.TokenGateway;
import lombok.RequiredArgsConstructor;

/**
 * Reconoce al usuario de un token emitido por crediya-autenticacion-ms.
 * Solo orquesta: las validaciones están en ValidadorToken.
 */
@RequiredArgsConstructor
public class AutenticacionUseCase {

    private final TokenGateway tokenGateway;

    public UsuarioAutenticado validarToken(String token) {
        ValidadorToken.validarPresente(token);
        return ValidadorToken.validarVigente(tokenGateway.validar(token));
    }
}
