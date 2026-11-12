package co.com.crediya.usecase.solicitud;

import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.estado.gateways.EstadoRepository;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.model.exceptions.MontoFueraDeRangoException;
import co.com.crediya.model.exceptions.TechnicalException;
import co.com.crediya.model.exceptions.TipoPrestamoNoExisteException;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.model.transaction.gateways.TransactionalGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern DOCUMENTO_PATTERN = Pattern.compile("^[A-Za-z0-9]{5,20}$");
    private static final int PLAZO_MINIMO_MESES = 1;
    private static final int PLAZO_MAXIMO_MESES = 360;

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoRepository estadoRepository;
    private final TransactionalGateway transactionalGateway;

    public Mono<Solicitud> registrar(Solicitud solicitud) {
        return Mono.fromCallable(() -> {
                    validar(solicitud);
                    return normalizar(solicitud);
                })
                // Validación del tipo, búsqueda del estado inicial y guardado en una sola transacción reactiva
                .flatMap(normalizada -> transactionalGateway.execute(registrarEnTransaccion(normalizada)));
    }

    private Mono<Solicitud> registrarEnTransaccion(Solicitud solicitud) {
        return tipoPrestamoRepository.findById(solicitud.getIdTipoPrestamo())
                .switchIfEmpty(Mono.error(() -> new TipoPrestamoNoExisteException(solicitud.getIdTipoPrestamo())))
                .flatMap(tipo -> validarMontoEnRango(solicitud, tipo))
                .flatMap(tipo -> estadoRepository.findByNombre(Estado.PENDIENTE_REVISION)
                        .switchIfEmpty(Mono.error(() -> new TechnicalException("ESTADO_INICIAL_NO_CONFIGURADO",
                                "No existe el estado '" + Estado.PENDIENTE_REVISION + "' en la base de datos")))
                        .flatMap(estado -> solicitudRepository.save(solicitud.toBuilder()
                                        .idEstado(estado.getIdEstado())
                                        .build())
                                .map(guardada -> guardada.toBuilder()
                                        .estado(estado.getNombre())
                                        .tipoPrestamo(tipo.getNombre())
                                        .build())));
    }

    private Mono<TipoPrestamo> validarMontoEnRango(Solicitud solicitud, TipoPrestamo tipo) {
        BigDecimal monto = solicitud.getMonto();
        if (monto.compareTo(tipo.getMontoMinimo()) < 0 || monto.compareTo(tipo.getMontoMaximo()) > 0) {
            return Mono.error(new MontoFueraDeRangoException(tipo.getNombre(), tipo.getMontoMinimo(), tipo.getMontoMaximo()));
        }
        return Mono.just(tipo);
    }

    private Solicitud normalizar(Solicitud solicitud) {
        return solicitud.toBuilder()
                .idSolicitud(null)
                .idEstado(null)
                .estado(null)
                .tipoPrestamo(null)
                .email(solicitud.getEmail().trim().toLowerCase(Locale.ROOT))
                .documentoIdentidad(solicitud.getDocumentoIdentidad().trim())
                .build();
    }

    private void validar(Solicitud solicitud) {
        if (solicitud == null) {
            throw new DatosInvalidosException(List.of("El cuerpo de la petición es obligatorio"));
        }

        List<String> errores = new ArrayList<>();

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
        if (solicitud.getIdTipoPrestamo() == null) {
            errores.add("El campo idTipoPrestamo es obligatorio");
        } else if (solicitud.getIdTipoPrestamo() <= 0) {
            errores.add("El campo idTipoPrestamo debe ser un número positivo");
        }

        if (!errores.isEmpty()) {
            throw new DatosInvalidosException(errores);
        }
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }
}
