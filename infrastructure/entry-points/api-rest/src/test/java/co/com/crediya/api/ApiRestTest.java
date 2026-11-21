package co.com.crediya.api;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.exceptions.AccesoDenegadoException;
import co.com.crediya.model.exceptions.DatosInvalidosException;
import co.com.crediya.model.exceptions.MontoFueraDeRangoException;
import co.com.crediya.model.exceptions.NoAutenticadoException;
import co.com.crediya.model.exceptions.TechnicalException;
import co.com.crediya.model.exceptions.TipoPrestamoNoExisteException;
import co.com.crediya.model.rol.Rol;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.usecase.autenticacion.AutenticacionUseCase;
import co.com.crediya.usecase.solicitud.SolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TestConfig.class)
class ApiRestTest {

    private static final String PATH = "/api/v1/solicitud";
    private static final String BODY = """
            {
              "monto": 5000000,
              "plazo": 24,
              "email": "juan@correo.com",
              "documentoIdentidad": "1020304050",
              "idTipoPrestamo": 1
            }
            """;

    @Autowired
    private WebApplicationContext context;

    private static final String TOKEN = "Bearer token-cliente";
    private static final UsuarioAutenticado CLIENTE =
            new UsuarioAutenticado(7L, "juan@correo.com", "1020304050", Rol.CLIENTE);

    @MockitoBean
    private SolicitudUseCase solicitudUseCase;

    @MockitoBean
    private AutenticacionUseCase autenticacionUseCase;

    private RestTestClient client;

    @BeforeEach
    void setup() {
        client = RestTestClient.bindToApplicationContext(context).build();
        when(autenticacionUseCase.validarToken(TOKEN)).thenReturn(CLIENTE);
    }

    @Test
    void registraSolicitudYRetorna201ConEstadoPendiente() {
        Solicitud creada = Solicitud.builder()
                .idSolicitud(1L).monto(new BigDecimal("5000000")).plazo(24)
                .email("juan@correo.com").documentoIdentidad("1020304050")
                .idTipoPrestamo(1L).tipoPrestamo("Libre inversión")
                .idEstado(1L).estado("Pendiente de revisión")
                .build();
        when(solicitudUseCase.registrar(any(), eq(CLIENTE))).thenReturn(creada);

        client.post().uri(PATH)
                .header("Authorization", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BODY)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location(PATH + "/1")
                .expectBody()
                .jsonPath("$.idSolicitud").isEqualTo(1)
                .jsonPath("$.estado").isEqualTo("Pendiente de revisión")
                .jsonPath("$.tipoPrestamo").isEqualTo("Libre inversión");
    }

    @Test
    void retorna400SiLosDatosSonInvalidos() {
        when(solicitudUseCase.registrar(any(), eq(CLIENTE)))
                .thenThrow(new DatosInvalidosException(List.of("El campo monto es obligatorio")));

        client.post().uri(PATH)
                .header("Authorization", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BODY)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("DATOS_INVALIDOS")
                .jsonPath("$.detalles[0]").isEqualTo("El campo monto es obligatorio");
    }

    @Test
    void retorna400SiElCuerpoEstaVacio() {
        client.post().uri(PATH)
                .header("Authorization", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void retorna400SiElJsonEsInvalido() {
        client.post().uri(PATH)
                .header("Authorization", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{ \"monto\": \"mucho\" }")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("JSON_INVALIDO");
    }

    @Test
    void retorna422SiElTipoDePrestamoNoExiste() {
        when(solicitudUseCase.registrar(any(), eq(CLIENTE))).thenThrow(new TipoPrestamoNoExisteException(99L));

        client.post().uri(PATH)
                .header("Authorization", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BODY)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("TIPO_PRESTAMO_NO_EXISTE");
    }

    @Test
    void retorna422SiElMontoEstaFueraDeRango() {
        when(solicitudUseCase.registrar(any(), eq(CLIENTE)))
                .thenThrow(new MontoFueraDeRangoException("Libre inversión", BigDecimal.ONE, BigDecimal.TEN));

        client.post().uri(PATH)
                .header("Authorization", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BODY)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("MONTO_FUERA_DE_RANGO");
    }

    @Test
    void retorna415SiElContentTypeNoEsJson() {
        client.post().uri(PATH)
                .header("Authorization", TOKEN)
                .contentType(MediaType.TEXT_PLAIN)
                .body("texto")
                .exchange()
                .expectStatus().isEqualTo(415)
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("TIPO_CONTENIDO_NO_SOPORTADO");
    }

    @Test
    void retorna500GenericoSiHayErrorTecnico() {
        when(solicitudUseCase.registrar(any(), eq(CLIENTE)))
                .thenThrow(new TechnicalException("ESTADO_INICIAL_NO_CONFIGURADO", "detalle interno"));

        client.post().uri(PATH)
                .header("Authorization", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BODY)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("ERROR_INTERNO");
    }

    @Test
    void retorna500GenericoSinExponerElDetalleDelError() {
        when(solicitudUseCase.registrar(any(), eq(CLIENTE))).thenThrow(new IllegalStateException("password=secreto"));

        client.post().uri(PATH)
                .header("Authorization", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BODY)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.mensaje").isEqualTo("Ocurrió un error inesperado. Intenta de nuevo más tarde");
    }

    @Test
    void retorna401SinToken() {
        when(autenticacionUseCase.validarToken(null)).thenThrow(new NoAutenticadoException("Debes iniciar sesión"));

        client.post().uri(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BODY)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().valueEquals("WWW-Authenticate", "Bearer")
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("NO_AUTENTICADO");

        verify(solicitudUseCase, never()).registrar(any(), any());
    }

    @Test
    void retorna403SiNoEsClienteOLaSolicitudEsDeOtro() {
        when(solicitudUseCase.registrar(any(), eq(CLIENTE)))
                .thenThrow(new AccesoDenegadoException("Solo puedes crear solicitudes de préstamo a tu nombre"));

        client.post().uri(PATH)
                .header("Authorization", TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BODY)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.codigo").isEqualTo("ACCESO_DENEGADO");
    }
}
