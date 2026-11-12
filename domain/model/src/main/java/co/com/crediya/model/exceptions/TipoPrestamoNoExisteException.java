package co.com.crediya.model.exceptions;

public class TipoPrestamoNoExisteException extends BusinessException {
    public TipoPrestamoNoExisteException(Long idTipoPrestamo) {
        super("TIPO_PRESTAMO_NO_EXISTE",
                "El tipo de préstamo con id " + idTipoPrestamo + " no existe");
    }
}
