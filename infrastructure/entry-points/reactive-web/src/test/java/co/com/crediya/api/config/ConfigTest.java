package co.com.crediya.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

class ConfigTest {

    private final WebTestClient client = WebTestClient
            .bindToRouterFunction(RouterFunctions.route(GET("/ping"), request -> ServerResponse.ok().bodyValue("pong")))
            .handlerStrategies(HandlerStrategies.builder()
                    .webFilter(new CorsConfig().corsWebFilter(List.of("http://localhost:4200")))
                    .webFilter(new SecurityHeadersConfig())
                    .build())
            .build();

    @Test
    void agregaCabecerasDeSeguridad() {
        client.get().uri("/ping")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().doesNotExist("Server")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

    @Test
    void permiteOrigenesConfiguradosEnCors() {
        client.get().uri("/ping")
                .header(HttpHeaders.ORIGIN, "http://localhost:4200")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:4200");
    }
}
