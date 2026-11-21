package co.com.crediya.api;

import co.com.crediya.api.dto.RegistrarSolicitudRequest;
import co.com.crediya.api.dto.SolicitudResponse;
import co.com.crediya.api.validacion.ValidadorPeticion;
import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.solicitud.Solicitud;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Entrada HTTP del registro de solicitudes de préstamo: recibe la petición, la delega y arma la respuesta.
 * Las validaciones de la petición están en ValidadorPeticion y las reglas de negocio en SolicitudUseCase.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/solicitud", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ApiRest {

    private final SolicitudUseCase solicitudUseCase;
    private final ValidadorPeticion validadorPeticion;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitudResponse> registrarSolicitudPrestamo(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestBody(required = false) RegistrarSolicitudRequest request) {
        UsuarioAutenticado solicitante = validadorPeticion.validarPeticionAutenticada(authorization, request);
        log.info("Registro de solicitud (tipo={}, plazo={}) del usuario {}",
                request.idTipoPrestamo(), request.plazo(), solicitante.idUsuario());

        Solicitud creada = solicitudUseCase.registrarSolicitudPrestamo(request.aDominio(), solicitante);

        log.info("Solicitud {} registrada con estado '{}'", creada.getIdSolicitud(), creada.getEstado());
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getIdSolicitud()).toUri();
        return ResponseEntity.created(ubicacion).body(SolicitudResponse.desdeDominio(creada));
    }
}
