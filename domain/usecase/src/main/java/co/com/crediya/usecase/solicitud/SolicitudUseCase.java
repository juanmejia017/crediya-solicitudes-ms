package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.exceptions.TechnicalException;
import co.com.crediya.model.exceptions.TipoPrestamoNoExisteException;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.SolicitudValidator;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoRepository estadoRepository;

    public Solicitud registrar(Solicitud solicitud) {
        SolicitudValidator.validar(solicitud);
        Solicitud normalizada = solicitud.normalizada();
        TipoPrestamo tipo = buscarTipoPrestamo(normalizada.getIdTipoPrestamo());
        tipo.validarMonto(normalizada.getMonto());
        Estado estadoInicial = buscarEstadoInicial();
        Solicitud guardada = solicitudRepository.save(normalizada.conEstado(estadoInicial));
        return guardada.conDescripciones(estadoInicial, tipo);
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
