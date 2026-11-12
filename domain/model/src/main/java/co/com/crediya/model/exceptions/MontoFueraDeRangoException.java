package co.com.crediya.model.exceptions;

import java.math.BigDecimal;

public class MontoFueraDeRangoException extends BusinessException {
    public MontoFueraDeRangoException(String tipoPrestamo, BigDecimal minimo, BigDecimal maximo) {
        super("MONTO_FUERA_DE_RANGO",
                "Para el tipo de préstamo " + tipoPrestamo + " el monto debe estar entre "
                        + minimo.toPlainString() + " y " + maximo.toPlainString());
    }
}
