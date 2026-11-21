package co.com.crediya.model.solicitud;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;

import java.math.BigDecimal;
import java.util.Locale;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {
    private Long idSolicitud;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private String documentoIdentidad;
    private Long idEstado;
    private Long idTipoPrestamo;

    // Datos de solo lectura para la respuesta (no se persisten en la tabla solicitud)
    private String estado;
    private String tipoPrestamo;

    /**
     * Copia lista para registrar: sin id ni estado (los asigna el sistema),
     * sin datos de solo lectura, email en minúsculas y documento sin espacios.
     */
    public Solicitud normalizada() {
        return toBuilder()
                .idSolicitud(null)
                .idEstado(null)
                .estado(null)
                .tipoPrestamo(null)
                .email(email.trim().toLowerCase(Locale.ROOT))
                .documentoIdentidad(documentoIdentidad.trim())
                .build();
    }

    /**
     * Regla: la solicitud es del usuario si el email coincide con el suyo y, cuando el usuario tiene
     * documento registrado, también el documento. Se usa sobre la solicitud ya normalizada.
     */
    public boolean perteneceA(UsuarioAutenticado usuario) {
        String correoDelUsuario = usuario.correoElectronico() == null
                ? null : usuario.correoElectronico().trim().toLowerCase(Locale.ROOT);
        boolean mismoCorreo = email.equals(correoDelUsuario);
        boolean mismoDocumento = usuario.documentoIdentidad() == null
                || documentoIdentidad.equals(usuario.documentoIdentidad().trim());
        return mismoCorreo && mismoDocumento;
    }

    public Solicitud conEstado(Estado estadoAsignado) {
        return toBuilder().idEstado(estadoAsignado.getIdEstado()).build();
    }

    public Solicitud conDescripciones(Estado estadoAsignado, TipoPrestamo tipo) {
        return toBuilder()
                .estado(estadoAsignado.getNombre())
                .tipoPrestamo(tipo.getNombre())
                .build();
    }
}
