package co.com.crediya.model.solicitud;

import co.com.crediya.model.exceptions.DatosInvalidosException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reglas de formato de la solicitud de préstamo. Pertenecen al modelo porque valen
 * para cualquier caso de uso que cree o modifique una solicitud.
 */
public final class SolicitudValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern DOCUMENTO_PATTERN = Pattern.compile("^[A-Za-z0-9]{5,20}$");
    private static final int PLAZO_MINIMO_MESES = 1;
    private static final int PLAZO_MAXIMO_MESES = 360;

    private SolicitudValidator() {
    }

    public static void validar(Solicitud solicitud) {
        if (solicitud == null) {
            throw new DatosInvalidosException(List.of("El cuerpo de la petición es obligatorio"));
        }
        List<String> errores = new ArrayList<>();
        validarPrestamo(solicitud, errores);
        validarCliente(solicitud, errores);
        if (!errores.isEmpty()) {
            throw new DatosInvalidosException(errores);
        }
    }

    private static void validarPrestamo(Solicitud solicitud, List<String> errores) {
        if (solicitud.getMonto() == null) {
            errores.add("El campo monto es obligatorio");
        } else if (solicitud.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            errores.add("El campo monto debe ser mayor que 0");
        }
        if (solicitud.getPlazo() == null) {
            errores.add("El campo plazo es obligatorio");
        } else if (solicitud.getPlazo() < PLAZO_MINIMO_MESES || solicitud.getPlazo() > PLAZO_MAXIMO_MESES) {
            errores.add("El campo plazo debe estar entre " + PLAZO_MINIMO_MESES + " y " + PLAZO_MAXIMO_MESES + " meses");
        }
        if (solicitud.getIdTipoPrestamo() == null) {
            errores.add("El campo idTipoPrestamo es obligatorio");
        } else if (solicitud.getIdTipoPrestamo() <= 0) {
            errores.add("El campo idTipoPrestamo debe ser un número positivo");
        }
    }

    private static void validarCliente(Solicitud solicitud, List<String> errores) {
        if (estaVacio(solicitud.getEmail())) {
            errores.add("El campo email es obligatorio");
        } else if (!EMAIL_PATTERN.matcher(solicitud.getEmail().trim()).matches()) {
            errores.add("El campo email no tiene un formato válido");
        }
        if (estaVacio(solicitud.getDocumentoIdentidad())) {
            errores.add("El campo documentoIdentidad es obligatorio");
        } else if (!DOCUMENTO_PATTERN.matcher(solicitud.getDocumentoIdentidad().trim()).matches()) {
            errores.add("El campo documentoIdentidad debe tener entre 5 y 20 caracteres alfanuméricos");
        }
    }

    private static boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
