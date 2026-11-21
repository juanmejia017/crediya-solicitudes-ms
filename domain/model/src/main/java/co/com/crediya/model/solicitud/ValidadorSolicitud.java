package co.com.crediya.model.solicitud;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.exceptions.AccesoDenegadoException;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.model.rol.Rol;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validaciones de la solicitud de préstamo: que quien la crea sea un cliente, el formato de los datos
 * y que el cliente sea el titular (la solicitud va a su nombre). El caso de uso solo las llama en orden.
 */
public final class ValidadorSolicitud {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern DOCUMENTO_PATTERN = Pattern.compile("^[A-Za-z0-9]{5,20}$");
    private static final int PLAZO_MINIMO_MESES = 1;
    private static final int PLAZO_MAXIMO_MESES = 360;

    private ValidadorSolicitud() {
    }

    /**
     * Solo un usuario con rol CLIENTE que inició sesión puede crear solicitudes.
     */
    public static void validarRolCliente(UsuarioAutenticado solicitante) {
        if (solicitante == null || !solicitante.tieneAlgunRol(Rol.CLIENTE)) {
            throw new AccesoDenegadoException("Solo un cliente puede crear solicitudes de préstamo");
        }
    }

    /**
     * El cliente solo puede crear solicitudes a su nombre: el email y el documento deben ser los suyos.
     */
    public static void validarTitular(Solicitud solicitud, UsuarioAutenticado solicitante) {
        if (!solicitud.perteneceA(solicitante)) {
            throw new AccesoDenegadoException("Solo puedes crear solicitudes de préstamo a tu nombre: "
                    + "el email y el documento deben ser los de tu usuario");
        }
    }

    /**
     * Formato de los datos: monto, plazo, tipo de préstamo, email y documento.
     */
    public static void validarDatos(Solicitud solicitud) {
        if (solicitud == null) {
            throw new DatosInvalidosException(List.of("El cuerpo de la petición es obligatorio"));
        }
        List<String> errores = new ArrayList<>();
        validarDatosDelPrestamo(solicitud, errores);
        validarDatosDelCliente(solicitud, errores);
        if (!errores.isEmpty()) {
            throw new DatosInvalidosException(errores);
        }
    }

    private static void validarDatosDelPrestamo(Solicitud solicitud, List<String> errores) {
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

    private static void validarDatosDelCliente(Solicitud solicitud, List<String> errores) {
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
