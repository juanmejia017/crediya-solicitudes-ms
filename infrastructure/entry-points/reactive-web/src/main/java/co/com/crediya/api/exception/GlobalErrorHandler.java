package co.com.crediya.api.exception;

import co.com.crediya.api.dto.ErrorResponse;
import co.com.crediya.api.trace.TraceIdFilter;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.model.exceptions.MontoFueraDeRangoException;
import co.com.crediya.model.exceptions.TechnicalException;
import co.com.crediya.model.exceptions.TipoPrestamoNoExisteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Manejador global de errores para WebFlux: toda excepción se traduce a un ErrorResponse en JSON,
 * así el usuario de la API nunca recibe trazas ni mensajes inesperados.
 */
@Slf4j
@Component
@Order(-2) // Antes que el DefaultErrorWebExceptionHandler de Spring Boot (-1)
public class GlobalErrorHandler implements WebExceptionHandler {

    private final List<HttpMessageWriter<?>> messageWriters;

    public GlobalErrorHandler(ServerCodecConfigurer serverCodecConfigurer) {
        this.messageWriters = serverCodecConfigurer.getWriters();
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }
        String traceId = TraceIdFilter.traceIdDe(exchange);
        ErrorResponse body = construirRespuesta(ex, traceId);

        return ServerResponse.status(body.status())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .flatMap(response -> response.writeTo(exchange, new ResponseContext()));
    }

    private ErrorResponse construirRespuesta(Throwable ex, String traceId) {
        if (ex instanceof DatosInvalidosException datos) {
            log.warn("[{}] Datos inválidos: {}", traceId, datos.getErrores());
            return error(HttpStatus.BAD_REQUEST, datos.getCodigo(), datos.getMessage(), datos.getErrores(), traceId);
        }
        if (ex instanceof TipoPrestamoNoExisteException || ex instanceof MontoFueraDeRangoException) {
            BusinessException negocio = (BusinessException) ex;
            log.warn("[{}] Regla de negocio [{}]: {}", traceId, negocio.getCodigo(), negocio.getMessage());
            return error(HttpStatus.UNPROCESSABLE_CONTENT, negocio.getCodigo(), negocio.getMessage(), List.of(), traceId);
        }
        if (ex instanceof BusinessException negocio) {
            log.warn("[{}] Error de negocio [{}]: {}", traceId, negocio.getCodigo(), negocio.getMessage());
            return error(HttpStatus.UNPROCESSABLE_CONTENT, negocio.getCodigo(), negocio.getMessage(), List.of(), traceId);
        }
        if (ex instanceof TechnicalException tecnica) {
            log.error("[{}] Error técnico [{}]: {}", traceId, tecnica.getCodigo(), tecnica.getMessage());
            return errorInterno(traceId);
        }
        if (ex instanceof UnsupportedMediaTypeStatusException) {
            log.warn("[{}] Content-Type no soportado", traceId);
            return error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "TIPO_CONTENIDO_NO_SOPORTADO",
                    "El Content-Type debe ser application/json", List.of(), traceId);
        }
        if (ex instanceof ServerWebInputException) {
            log.warn("[{}] Cuerpo de la petición ilegible", traceId);
            return error(HttpStatus.BAD_REQUEST, "JSON_INVALIDO",
                    "El cuerpo de la petición es inválido. Revisa que monto sea numérico y plazo e idTipoPrestamo sean enteros",
                    List.of(), traceId);
        }
        if (ex instanceof MethodNotAllowedException) {
            return error(HttpStatus.METHOD_NOT_ALLOWED, "METODO_NO_PERMITIDO",
                    "El método HTTP no está permitido para este recurso", List.of(), traceId);
        }
        if (ex instanceof ResponseStatusException status && status.getStatusCode().is4xxClientError()) {
            HttpStatusCode code = status.getStatusCode();
            log.warn("[{}] Error del cliente {}", traceId, code.value());
            return code.value() == HttpStatus.NOT_FOUND.value()
                    ? error(HttpStatus.NOT_FOUND, "RECURSO_NO_ENCONTRADO", "El recurso solicitado no existe", List.of(), traceId)
                    : ErrorResponse.of(code.value(), "PETICION_INVALIDA", "La petición no es válida", List.of(), traceId);
        }
        log.error("[{}] Error inesperado", traceId, ex);
        return errorInterno(traceId);
    }

    private ErrorResponse errorInterno(String traceId) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO",
                "Ocurrió un error inesperado. Intenta de nuevo más tarde", List.of(), traceId);
    }

    private ErrorResponse error(HttpStatus status, String codigo, String mensaje, List<String> detalles, String traceId) {
        return ErrorResponse.of(status.value(), codigo, mensaje, detalles, traceId);
    }

    private class ResponseContext implements ServerResponse.Context {
        @Override
        public List<HttpMessageWriter<?>> messageWriters() {
            return messageWriters;
        }

        @Override
        public List<ViewResolver> viewResolvers() {
            return List.of();
        }
    }
}
