package co.com.crediya.api.validacion;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.model.exceptions.NoAutenticadoException;
import co.com.crediya.usecase.autenticacion.AutenticacionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Valida la petición HTTP antes de llegar al caso de uso: saca el token del header
 * Authorization ("Bearer <token>"), lo hace validar y revisa que el cuerpo no venga vacío.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ValidadorPeticion {

    private static final String PREFIJO_BEARER = "Bearer ";

    private final AutenticacionUseCase autenticacionUseCase;

    /**
     * Valida el token y luego el cuerpo. Devuelve quién hizo la petición.
     */
    public UsuarioAutenticado validarPeticionAutenticada(String authorization, Object cuerpo) {
        UsuarioAutenticado solicitante = autenticacionUseCase.validarToken(extraerToken(authorization));
        log.info("Petición del usuario {} con rol {}", solicitante.idUsuario(), solicitante.rol());
        validarCuerpo(cuerpo);
        return solicitante;
    }

    public void validarCuerpo(Object cuerpo) {
        if (cuerpo == null) {
            throw new DatosInvalidosException(List.of("El cuerpo de la petición es obligatorio"));
        }
    }

    private static String extraerToken(String authorization) {
        if (authorization == null || !authorization.startsWith(PREFIJO_BEARER)) {
            throw new NoAutenticadoException("Debes iniciar sesión y enviar el token en el header Authorization: Bearer <token>");
        }
        return authorization.substring(PREFIJO_BEARER.length()).trim();
    }
}
