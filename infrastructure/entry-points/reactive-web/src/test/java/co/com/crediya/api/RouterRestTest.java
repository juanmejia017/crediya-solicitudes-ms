package co.com.crediya.api;

import co.com.crediya.api.exception.GlobalErrorHandler;
import co.com.crediya.api.trace.TraceIdFilter;
import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.model.transaction.gateways.TransactionalGateway;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

class RouterRestTest {

    private static final String PATH = RouterRest.SOLICITUD_PATH;
    private static final String BODY_VALIDO = """
            {
              "monto": 5000000,
              "plazo": 24,
              "email": "juan@correo.com",
              "documentoIdentidad": "1020304050",
              "idTipoPrestamo": 1
            }
            """;

    private final AtomicLong secuencia = new AtomicLong();
    private Function<Solicitud, Mono<Solicitud>> guardado;
    private WebTestClient client;

    @BeforeEach
    void setUp() {
        guardado = solicitud -> Mono.just(solicitud.toBuilder().idSolicitud(secuencia.incrementAndGet()).build());

        TipoPrestamo libreInversion = TipoPrestamo.builder()
                .idTipoPrestamo(1L).nombre("Libre inversión")
                .montoMinimo(new BigDecimal("1000000")).montoMaximo(new BigDecimal("50000000"))
                .tasaInteres(new BigDecimal("1.80")).validacionAutomatica(true)
                .build();
        Estado pendiente = Estado.builder().idEstado(1L).nombre(Estado.PENDIENTE_REVISION).build();

        TransactionalGateway transaccion = new TransactionalGateway() {
            @Override
            public <T> Mono<T> execute(Mono<T> operation) {
                return operation;
            }
        };

        SolicitudUseCase useCase = new SolicitudUseCase(
                solicitud -> guardado.apply(solicitud),
                id -> id == 1L ? Mono.just(libreInversion) : Mono.empty(),
                nombre -> Estado.PENDIENTE_REVISION.equals(nombre) ? Mono.just(pendiente) : Mono.empty(),
                transaccion);

        HandlerStrategies strategies = HandlerStrategies.builder()
                .webFilter(new TraceIdFilter())
                .exceptionHandler(new GlobalErrorHandler(ServerCodecConfigurer.create()))
                .build();

        client = WebTestClient
                .bindToRouterFunction(new RouterRest().routerFunction(new Handler(useCase)))
                .handlerStrategies(strategies)
                .build();
    }

    @Test
    void registraSolicitudYRetorna201ConEstadoPendiente() {
        client.post().uri(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(BODY_VALIDO)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", PATH + "/1")
                .expectHeader().exists(TraceIdFilter.TRACE_HEADER)
                .expectBody()
                .jsonPath("$.idSolicitud").isEqualTo(1)
                .jsonPath("$.estado").isEqualTo(Estado.PENDIENTE_REVISION)
                .jsonPath("$.idEstado").isEqualTo(1)
                .jsonPath("$.tipoPrestamo").isEqualTo("Libre inversión")
                .jsonPath("$.email").isEqualTo("juan@correo.com");
    }

    @Test
    void reutilizaElTraceIdEnviadoPorElCliente() {
        client.post().uri(PATH)
                .header(TraceIdFilter.TRACE_HEADER, "prueba-traza-123")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(BODY_VALIDO)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals(TraceIdFilter.TRACE_HEADER, "prueba-traza-123");
    }

    @Test
    void retorna400ConDetallesSiFaltanDatos() {
        client.post().uri(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("DATOS_INVALIDOS")
                .jsonPath("$.detalles.length()").isEqualTo(5)
                .jsonPath("$.traceId").exists();
    }

    @Test
    void retorna400SiElCuerpoEstaVacio() {
        client.post().uri(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("DATOS_INVALIDOS");
    }

    @Test
    void retorna400SiElJsonEsInvalido() {
        client.post().uri(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"monto\": \"mucho\", \"plazo\": 12}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("JSON_INVALIDO");
    }

    @Test
    void retorna422SiElTipoDePrestamoNoExiste() {
        client.post().uri(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(BODY_VALIDO.replace("\"idTipoPrestamo\": 1", "\"idTipoPrestamo\": 99"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("TIPO_PRESTAMO_NO_EXISTE");
    }

    @Test
    void retorna422SiElMontoEstaFueraDelRangoDelTipo() {
        client.post().uri(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(BODY_VALIDO.replace("5000000", "999000000"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("MONTO_FUERA_DE_RANGO");
    }

    @Test
    void retorna415SiElContentTypeNoEsJson() {
        client.post().uri(PATH)
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("hola")
                .exchange()
                .expectStatus().isEqualTo(415)
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("TIPO_CONTENIDO_NO_SOPORTADO");
    }

    @Test
    void retorna500GenericoSinExponerElDetalleDelError() {
        guardado = solicitud -> Mono.error(new IllegalStateException("conexión rechazada: host=db password=secreto"));

        client.post().uri(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(BODY_VALIDO)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("ERROR_INTERNO")
                .jsonPath("$.mensaje").isEqualTo("Ocurrió un error inesperado. Intenta de nuevo más tarde");
    }
}
