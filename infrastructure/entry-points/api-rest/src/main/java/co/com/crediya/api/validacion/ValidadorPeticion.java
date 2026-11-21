package co.com.crediya.api.validacion;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.usecase.autenticacion.AutenticacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Validaciones de la petición HTTP antes de llegar al caso de uso:
 * que traiga un token válido y que el cuerpo no venga vacío.
 */
@Component
@RequiredArgsConstructor
public class ValidadorPeticion {

    private final AutenticacionUseCase autenticacionUseCase;

    /**
     * Valida el token del header Authorization y luego el cuerpo. Devuelve quién hizo la petición.
     */
    public UsuarioAutenticado validarPeticionAutenticada(String authorization, Object cuerpo) {
        UsuarioAutenticado solicitante = autenticacionUseCase.validarToken(authorization);
        validarCuerpo(cuerpo);
        return solicitante;
    }

    public void validarCuerpo(Object cuerpo) {
        if (cuerpo == null) {
            throw new DatosInvalidosException(List.of("El cuerpo de la petición es obligatorio"));
        }
    }
}
