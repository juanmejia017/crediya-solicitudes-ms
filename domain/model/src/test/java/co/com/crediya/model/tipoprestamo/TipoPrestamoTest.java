package co.com.crediya.model.tipoprestamo;

import co.com.crediya.model.exceptions.MontoFueraDeRangoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TipoPrestamoTest {

    private final TipoPrestamo libreInversion = TipoPrestamo.builder()
            .idTipoPrestamo(1L).nombre("Libre inversión")
            .montoMinimo(new BigDecimal("1000000")).montoMaximo(new BigDecimal("50000000"))
            .build();

    @Test
    void aceptaMontosDentroDelRangoYEnLosLimites() {
        assertDoesNotThrow(() -> libreInversion.validarMonto(new BigDecimal("1000000")));
        assertDoesNotThrow(() -> libreInversion.validarMonto(new BigDecimal("25000000")));
        assertDoesNotThrow(() -> libreInversion.validarMonto(new BigDecimal("50000000")));
    }

    @Test
    void rechazaMontoMenorAlMinimo() {
        BigDecimal monto = new BigDecimal("999999");

        assertThrows(MontoFueraDeRangoException.class, () -> libreInversion.validarMonto(monto));
    }

    @Test
    void rechazaMontoMayorAlMaximo() {
        BigDecimal monto = new BigDecimal("50000001");

        assertThrows(MontoFueraDeRangoException.class, () -> libreInversion.validarMonto(monto));
    }
}
