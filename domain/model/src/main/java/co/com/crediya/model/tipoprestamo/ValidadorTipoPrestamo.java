package co.com.crediya.model.tipoprestamo;

import co.com.crediya.model.exceptions.MontoFueraDeRangoException;
import co.com.crediya.model.exceptions.TipoPrestamoNoExisteException;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Validaciones del tipo de préstamo: que exista y que permita el monto pedido.
 */
public final class ValidadorTipoPrestamo {

    private ValidadorTipoPrestamo() {
    }

    public static TipoPrestamo validarExistencia(Optional<TipoPrestamo> tipo, Long idTipoPrestamo) {
        return tipo.orElseThrow(() -> new TipoPrestamoNoExisteException(idTipoPrestamo));
    }

    public static void validarMonto(TipoPrestamo tipo, BigDecimal monto) {
        if (!tipo.permiteMonto(monto)) {
            throw new MontoFueraDeRangoException(tipo.getNombre(), tipo.getMontoMinimo(), tipo.getMontoMaximo());
        }
    }
}
