package co.com.crediya.security;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.rol.Rol;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenAdapterTest {

    private static final String SECRETO = "clave-secreta-de-pruebas-con-mas-de-32-caracteres";
    private static final long AHORA = Instant.parse("2026-11-21T12:00:00Z").getEpochSecond();
    private static final String HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

    private final JwtTokenAdapter adapter =
            new JwtTokenAdapter(SECRETO, Clock.fixed(Instant.ofEpochSecond(AHORA), ZoneOffset.UTC));

    /** Arma un token igual al que genera crediya-autenticacion-ms. */
    private static String armarToken(String header, String payload, String secreto) throws Exception {
        Base64.Encoder b64 = Base64.getUrlEncoder().withoutPadding();
        String contenido = b64.encodeToString(header.getBytes(StandardCharsets.UTF_8)) + "."
                + b64.encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return contenido + "." + b64.encodeToString(mac.doFinal(contenido.getBytes(StandardCharsets.UTF_8)));
    }

    private static String armarContenido(String emisor, String rol, long exp) {
        return "{\"iss\":\"" + emisor + "\",\"sub\":\"ana@correo.com\",\"idUsuario\":7,\"documento\":\"1020304050\","
                + "\"rol\":\"" + rol + "\",\"iat\":" + (AHORA - 60) + ",\"exp\":" + exp + "}";
    }

    @Test
    void aceptaUnTokenValidoDeAutenticacion() throws Exception {
        String token = armarToken(HEADER, armarContenido("crediya-autenticacion-ms", "CLIENTE", AHORA + 3600), SECRETO);

        assertEquals(Optional.of(new UsuarioAutenticado(7L, "ana@correo.com", "1020304050", Rol.CLIENTE)),
                adapter.validar(token));
    }

    @Test
    void rechazaTokenExpirado() throws Exception {
        assertTrue(adapter.validar(armarToken(HEADER, armarContenido("crediya-autenticacion-ms", "CLIENTE", AHORA), SECRETO)).isEmpty());
    }

    @Test
    void rechazaOtroSecreto() throws Exception {
        String token = armarToken(HEADER, armarContenido("crediya-autenticacion-ms", "CLIENTE", AHORA + 3600),
                "otro-secreto-distinto-tambien-de-mas-de-32");

        assertTrue(adapter.validar(token).isEmpty());
    }

    @Test
    void rechazaOtroEmisor() throws Exception {
        assertTrue(adapter.validar(armarToken(HEADER, armarContenido("otro-sistema", "CLIENTE", AHORA + 3600), SECRETO)).isEmpty());
    }

    @Test
    void rechazaAlgoritmoDistinto() throws Exception {
        String token = armarToken("{\"alg\":\"none\"}", armarContenido("crediya-autenticacion-ms", "CLIENTE", AHORA + 3600), SECRETO);

        assertTrue(adapter.validar(token).isEmpty());
    }

    @Test
    void rechazaRolDesconocidoYBasura() throws Exception {
        assertTrue(adapter.validar(armarToken(HEADER, armarContenido("crediya-autenticacion-ms", "JEFE", AHORA + 3600), SECRETO)).isEmpty());
        assertTrue(adapter.validar("no-es-un-token").isEmpty());
        assertTrue(adapter.validar("a.b.c").isEmpty());
    }

    @Test
    void exigeUnSecretoDeAlMenos32Caracteres() {
        assertThrows(IllegalArgumentException.class, () -> new JwtTokenAdapter("corto"));
    }
}
