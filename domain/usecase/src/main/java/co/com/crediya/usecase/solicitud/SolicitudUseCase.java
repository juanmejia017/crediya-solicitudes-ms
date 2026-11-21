package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.exceptions.AccesoDenegadoException;
import co.com.crediya.model.exceptions.TechnicalException;
import co.com.crediya.model.exceptions.TipoPrestamoNoExisteException;
import co.com.crediya.model.rol.Rol;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.SolicitudValidator;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoRepository estadoRepository;

    /**
     * Registra una solicitud. Solo la puede crear un cliente que inició sesión, y solo a su nombre.
     */
    public Solicitud registrar(Solicitud solicitud, UsuarioAutenticado solicitante) {
        validarQueSeaCliente(solicitante);
        SolicitudValidator.validar(solicitud);
        Solicitud normalizada = solicitud.normalizada();
        validarQueSeaPropia(normalizada, solicitante);
        TipoPrestamo tipo = buscarTipoPrestamo(normalizada.getIdTipoPrestamo());
        tipo.validarMonto(normalizada.getMonto());
        Estado estadoInicial = buscarEstadoInicial();
        Solicitud guardada = solicitudRepository.save(normalizada.conEstado(estadoInicial));
        return guardada.conDescripciones(estadoInicial, tipo);
    }

    private static void validarQueSeaCliente(UsuarioAutenticado solicitante) {
        if (solicitante == null || !solicitante.tieneAlgunRol(Rol.CLIENTE)) {
            throw new AccesoDenegadoException("Solo un cliente puede crear solicitudes de préstamo");
        }
    }

    /**
     * El email (y el documento, si el cliente lo tiene registrado) deben ser los del usuario del token.
     */
    private static void validarQueSeaPropia(Solicitud solicitud, UsuarioAutenticado solicitante) {
        String correoDelToken = solicitante.correoElectronico() == null
                ? null : solicitante.correoElectronico().trim().toLowerCase(Locale.ROOT);
        boolean mismoCorreo = solicitud.getEmail().equals(correoDelToken);
        boolean mismoDocumento = solicitante.documentoIdentidad() == null
                || solicitud.getDocumentoIdentidad().equals(solicitante.documentoIdentidad().trim());
        if (!mismoCorreo || !mismoDocumento) {
            throw new AccesoDenegadoException("Solo puedes crear solicitudes de préstamo a tu nombre: "
                    + "el email y el documento deben ser los de tu usuario");
        }
    }

    private TipoPrestamo buscarTipoPrestamo(Long idTipoPrestamo) {
        return tipoPrestamoRepository.findByIdTipoPrestamo(idTipoPrestamo)
                .orElseThrow(() -> new TipoPrestamoNoExisteException(idTipoPrestamo));
    }

    private Estado buscarEstadoInicial() {
        return estadoRepository.findByNombre(Estado.PENDIENTE_REVISION)
                .orElseThrow(() -> new TechnicalException("ESTADO_INICIAL_NO_CONFIGURADO",
                        "No existe el estado '" + Estado.PENDIENTE_REVISION + "' en la base de datos"));
    }
}
