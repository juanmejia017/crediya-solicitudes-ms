package co.com.crediya.security;

import co.com.crediya.model.auth.UsuarioAutenticado;
import co.com.crediya.model.auth.gateways.TokenGateway;
import co.com.crediya.model.rol.Rol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Clock;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

/**
 * Valida los tokens JWT (HS256) que emite crediya-autenticacion-ms: firma con el mismo secreto
 * (variable JWT_SECRET), emisor, vencimiento y rol. Este microservicio no genera tokens.
 */
@Slf4j
@Component
public class JwtTokenAdapter implements TokenGateway {

    static final String EMISOR = "crediya-autenticacion-ms";
    private static final String ALGORITMO = "HmacSHA256";
    private static final int SECRETO_MINIMO_BYTES = 32;

    private final byte[] secreto;
    private final Clock clock;

    @Autowired
    public JwtTokenAdapter(@Value("${security.jwt.secret}") String secreto) {
        this(secreto, Clock.systemUTC());
    }

    JwtTokenAdapter(String secreto, Clock clock) {
        if (secreto == null || secreto.getBytes(StandardCharsets.UTF_8).length < SECRETO_MINIMO_BYTES) {
            throw new IllegalArgumentException("security.jwt.secret (JWT_SECRET) debe tener al menos 32 caracteres");
        }
        this.secreto = secreto.getBytes(StandardCharsets.UTF_8);
        this.clock = clock;
    }

    @Override
    public Optional<UsuarioAutenticado> validar(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                return rechazar("formato inválido");
            }
            byte[] firmaEsperada = firmar(partes[0] + "." + partes[1]);
            if (!MessageDigest.isEqual(firmaEsperada, decodificar(partes[2]))) {
                return rechazar("firma inválida");
            }
            Map<String, Object> header = JsonPlano.leer(new String(decodificar(partes[0]), StandardCharsets.UTF_8));
            if (!"HS256".equals(header.get("alg"))) {
                return rechazar("algoritmo no permitido");
            }
            Map<String, Object> claims = JsonPlano.leer(new String(decodificar(partes[1]), StandardCharsets.UTF_8));
            if (!EMISOR.equals(claims.get("iss"))) {
                return rechazar("emisor desconocido");
            }
            if (!(claims.get("exp") instanceof Long exp) || exp <= clock.instant().getEpochSecond()) {
                return rechazar("token expirado");
            }
            Optional<Rol> rol = Rol.porNombre(String.valueOf(claims.get("rol")));
            if (rol.isEmpty() || !(claims.get("idUsuario") instanceof Long idUsuario)) {
                return rechazar("datos del usuario incompletos");
            }
            return Optional.of(new UsuarioAutenticado(idUsuario, (String) claims.get("sub"),
                    (String) claims.get("documento"), rol.get()));
        } catch (IllegalArgumentException | ClassCastException e) {
            return rechazar("contenido ilegible");
        }
    }

    private Optional<UsuarioAutenticado> rechazar(String motivo) {
        log.warn("Token rechazado: {}", motivo);
        return Optional.empty();
    }

    private byte[] firmar(String contenido) {
        try {
            Mac mac = Mac.getInstance(ALGORITMO);
            mac.init(new SecretKeySpec(secreto, ALGORITMO));
            return mac.doFinal(contenido.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No fue posible verificar la firma del token", e);
        }
    }

    private static byte[] decodificar(String texto) {
        return Base64.getUrlDecoder().decode(texto);
    }
}
