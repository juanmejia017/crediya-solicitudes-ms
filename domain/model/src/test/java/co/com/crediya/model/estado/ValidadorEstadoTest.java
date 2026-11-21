package co.com.crediya.model.estado;

import co.com.crediya.model.exceptions.TechnicalException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidadorEstadoTest {

    @Test
    void elEstadoInicialDebeExistir() {
        Estado pendiente = Estado.builder().idEstado(1L).nombre(Estado.PENDIENTE_REVISION).build();

        assertEquals(pendiente, ValidadorEstado.validarEstadoInicial(Optional.of(pendiente)));
        assertThrows(TechnicalException.class, () -> ValidadorEstado.validarEstadoInicial(Optional.empty()));
    }
}
