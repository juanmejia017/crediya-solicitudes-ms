package co.com.crediya.api;

import co.com.crediya.api.dto.RegistrarSolicitudRequest;
import co.com.crediya.api.dto.SolicitudResponse;
import co.com.crediya.api.trace.TraceIdFilter;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final SolicitudUseCase solicitudUseCase;

    public Mono<ServerResponse> registrarSolicitud(ServerRequest serverRequest) {
        String traceId = TraceIdFilter.traceIdDe(serverRequest.exchange());

        return serverRequest.bodyToMono(RegistrarSolicitudRequest.class)
                .switchIfEmpty(Mono.error(() ->
                        new DatosInvalidosException(List.of("El cuerpo de la petición es obligatorio"))))
                .doOnNext(request -> log.info("[{}] POST {} - inicio registro de solicitud (tipo={}, plazo={})",
                        traceId, RouterRest.SOLICITUD_PATH, request.idTipoPrestamo(), request.plazo()))
                .map(RegistrarSolicitudRequest::toDomain)
                .flatMap(solicitudUseCase::registrar)
                .doOnNext(creada -> log.info("[{}] POST {} - solicitud {} registrada con estado '{}'",
                        traceId, RouterRest.SOLICITUD_PATH, creada.getIdSolicitud(), creada.getEstado()))
                .flatMap(creada -> ServerResponse
                        .created(URI.create(RouterRest.SOLICITUD_PATH + "/" + creada.getIdSolicitud()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(SolicitudResponse.fromDomain(creada)));
    }
}
