package co.com.crediya.model.tipoprestamo;

import co.com.crediya.model.exceptions.TipoPrestamoNoExisteException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidadorTipoPrestamoTest {

    @Test
    void elTipoDePrestamoDebeExistir() {
        TipoPrestamo tipo = TipoPrestamo.builder().idTipoPrestamo(1L).nombre("Libre inversión").build();

        assertEquals(tipo, ValidadorTipoPrestamo.validarExistencia(Optional.of(tipo), 1L));
        assertThrows(TipoPrestamoNoExisteException.class, () -> ValidadorTipoPrestamo.validarExistencia(Optional.empty(), 99L));
    }
}
