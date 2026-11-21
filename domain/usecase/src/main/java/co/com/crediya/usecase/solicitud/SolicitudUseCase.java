package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.ValidadorEstado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.ValidadorSolicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.ValidadorTipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;

/**
 * Registro de solicitudes de préstamo. Solo orquesta: las validaciones están en
 * ValidadorSolicitud, ValidadorTipoPrestamo y ValidadorEstado.
 */
@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoRepository estadoRepository;

    public Solicitud registrarSolicitudPrestamo(Solicitud solicitud, UsuarioAutenticado solicitante) {
        ValidadorSolicitud.validarRolCliente(solicitante);
        ValidadorSolicitud.validarDatos(solicitud);
        Solicitud normalizada = solicitud.normalizada();
        ValidadorSolicitud.validarTitular(normalizada, solicitante);

        Long idTipoPrestamo = normalizada.getIdTipoPrestamo();
        TipoPrestamo tipo = ValidadorTipoPrestamo.validarExistencia(tipoPrestamoRepository.buscarPorId(idTipoPrestamo), idTipoPrestamo);
        ValidadorTipoPrestamo.validarMonto(tipo, normalizada.getMonto());
        Estado estadoInicial = ValidadorEstado.validarEstadoInicial(estadoRepository.buscarPorNombre(Estado.PENDIENTE_REVISION));

        Solicitud guardada = solicitudRepository.guardar(normalizada.conEstado(estadoInicial));
        return guardada.conDescripciones(estadoInicial, tipo);
    }
}
