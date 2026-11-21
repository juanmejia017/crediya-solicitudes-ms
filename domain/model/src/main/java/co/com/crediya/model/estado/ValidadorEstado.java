package co.com.crediya.model.estado;

import co.com.crediya.model.exceptions.TechnicalException;

import java.util.Optional;

/**
 * Validaciones del estado: el estado inicial "Pendiente de revisión" debe existir en la base de datos.
 */
public final class ValidadorEstado {

    private ValidadorEstado() {
    }

    public static Estado validarEstadoInicial(Optional<Estado> estado) {
        return estado.orElseThrow(() -> new TechnicalException("ESTADO_INICIAL_NO_CONFIGURADO",
                "No existe el estado '" + Estado.PENDIENTE_REVISION + "' en la base de datos"));
    }
}
