package co.com.crediya.model.exceptions;

/**
 * La petición no trae un token válido: hay que iniciar sesión.
 */
public class NoAutenticadoException extends BusinessException {
    public NoAutenticadoException(String mensaje) {
        super("NO_AUTENTICADO", mensaje);
    }
}
