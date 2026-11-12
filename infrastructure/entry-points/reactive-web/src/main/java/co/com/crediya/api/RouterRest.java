package co.com.crediya.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    public static final String SOLICITUD_PATH = "/api/v1/solicitud";

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(SOLICITUD_PATH), handler::registrarSolicitud);
    }
}
