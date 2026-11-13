package co.com.crediya.model.tipoprestamo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import co.com.crediya.model.exceptions.MontoFueraDeRangoException;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class TipoPrestamo {
    private Long idTipoPrestamo;
    private String nombre;
    private BigDecimal montoMinimo;
    private BigDecimal montoMaximo;
    private BigDecimal tasaInteres;
    private Boolean validacionAutomatica;

    /**
     * Regla del tipo de préstamo: el monto solicitado debe estar entre su mínimo y su máximo.
     */
    public void validarMonto(BigDecimal monto) {
        if (monto.compareTo(montoMinimo) < 0 || monto.compareTo(montoMaximo) > 0) {
            throw new MontoFueraDeRangoException(nombre, montoMinimo, montoMaximo);
        }
    }
}
