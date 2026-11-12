package co.com.crediya.model.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class DatosInvalidosException extends BusinessException {
    private final List<String> errores;

    public DatosInvalidosException(List<String> errores) {
        super("DATOS_INVALIDOS", "Los datos enviados no son válidos");
        this.errores = List.copyOf(errores);
    }
}
