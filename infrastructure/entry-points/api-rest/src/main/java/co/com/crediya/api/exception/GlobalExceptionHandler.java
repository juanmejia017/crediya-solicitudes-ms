package co.com.crediya.api.exception;

import co.com.crediya.api.dto.ErrorResponse;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.model.exceptions.MontoFueraDeRangoException;
import co.com.crediya.model.exceptions.TechnicalException;
import co.com.crediya.model.exceptions.TipoPrestamoNoExisteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<ErrorResponse> handleDatosInvalidos(DatosInvalidosException ex) {
        log.warn("Datos inválidos: {}", ex.getErrores());
        return build(HttpStatus.BAD_REQUEST, ex.getCodigo(), ex.getMessage(), ex.getErrores());
    }

    @ExceptionHandler({TipoPrestamoNoExisteException.class, MontoFueraDeRangoException.class})
    public ResponseEntity<ErrorResponse> handleReglaPrestamo(BusinessException ex) {
        log.warn("Regla de negocio [{}]: {}", ex.getCodigo(), ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_CONTENT, ex.getCodigo(), ex.getMessage(), List.of());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        log.warn("Error de negocio [{}]: {}", ex.getCodigo(), ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_CONTENT, ex.getCodigo(), ex.getMessage(), List.of());
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ErrorResponse> handleTechnical(TechnicalException ex) {
        log.error("Error técnico [{}]: {}", ex.getCodigo(), ex.getMessage());
        return errorInterno();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonInvalido(HttpMessageNotReadableException ex) {
        log.warn("Cuerpo de la petición ilegible");
        return build(HttpStatus.BAD_REQUEST, "JSON_INVALIDO",
                "El cuerpo de la petición es inválido. Revisa que monto sea numérico y plazo e idTipoPrestamo sean enteros",
                List.of());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "TIPO_CONTENIDO_NO_SOPORTADO",
                "El Content-Type debe ser application/json", List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMetodo(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "METODO_NO_PERMITIDO",
                "El método HTTP no está permitido para este recurso", List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoEncontrado(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "RECURSO_NO_ENCONTRADO", "El recurso solicitado no existe", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Error inesperado", ex);
        return errorInterno();
    }

    private ResponseEntity<ErrorResponse> errorInterno() {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO",
                "Ocurrió un error inesperado. Intenta de nuevo más tarde", List.of());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String codigo, String mensaje, List<String> detalles) {
        return ResponseEntity.status(status).body(ErrorResponse.of(status.value(), codigo, mensaje, detalles));
    }
}
