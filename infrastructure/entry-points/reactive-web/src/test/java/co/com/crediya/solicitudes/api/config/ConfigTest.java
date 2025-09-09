package co.com.crediya.solicitudes.api.config;

import co.com.crediya.solicitudes.api.RouterRest;
import co.com.crediya.solicitudes.api.SolicitudHandler;
import co.com.crediya.solicitudes.usecase.solicitud.ListSolicitudesPendientesUseCase;
import co.com.crediya.solicitudes.usecase.solicitud.SolicitudUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, SolicitudHandler.class})
@WebFluxTest(excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SolicitudUseCase solicitudUseCase;

    @MockBean
    private ListSolicitudesPendientesUseCase listSolicitudesPendientesUseCase;

    @Test
    void corsConfigurationShouldAllowOrigins() {
        when(listSolicitudesPendientesUseCase.execute(any(), any())).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/solicitud")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}