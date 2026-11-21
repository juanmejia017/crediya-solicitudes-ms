package co.com.crediya.model.exceptions;

/**
 * El usuario inició sesión, pero su rol no le permite hacer esta acción.
 */
public class AccesoDenegadoException extends BusinessException {
    public AccesoDenegadoException(String mensaje) {
        super("ACCESO_DENEGADO", mensaje);
    }
}
