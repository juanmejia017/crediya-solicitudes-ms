package co.com.crediya.model.exceptions;

import lombok.Getter;

/**
 * Error técnico o de configuración: no es culpa del usuario y no se le muestra el detalle.
 */
@Getter
public class TechnicalException extends RuntimeException {
    private final String codigo;

    public TechnicalException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }
}
