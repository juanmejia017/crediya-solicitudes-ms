package co.com.crediya.api;

import co.com.crediya.api.dto.RegistrarSolicitudRequest;
import co.com.crediya.api.dto.SolicitudResponse;
import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.autenticacion.AutenticacionUseCase;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = ApiRest.BASE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApiRest {

    static final String BASE_PATH = "/api/v1/solicitud";

    private final SolicitudUseCase solicitudUseCase;
    private final AutenticacionUseCase autenticacionUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitudResponse> registrar(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestBody(required = false) RegistrarSolicitudRequest request) {
        UsuarioAutenticado solicitante = autenticacionUseCase.validarToken(authorization);
        if (request == null) {
            throw new DatosInvalidosException(List.of("El cuerpo de la petición es obligatorio"));
        }
        log.info("POST {} - inicio registro de solicitud (tipo={}, plazo={}), solicitado por el usuario {} con rol {}",
                BASE_PATH, request.idTipoPrestamo(), request.plazo(), solicitante.idUsuario(), solicitante.rol());

        Solicitud creada = solicitudUseCase.registrar(request.toDomain(), solicitante);

        log.info("POST {} - solicitud {} registrada con estado '{}'",
                BASE_PATH, creada.getIdSolicitud(), creada.getEstado());
        return ResponseEntity
                .created(URI.create(BASE_PATH + "/" + creada.getIdSolicitud()))
                .body(SolicitudResponse.fromDomain(creada));
    }
}
