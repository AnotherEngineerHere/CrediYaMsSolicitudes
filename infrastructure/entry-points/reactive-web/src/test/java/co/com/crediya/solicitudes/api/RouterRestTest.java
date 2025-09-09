package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.RegistrarSolicitudRequest;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.usecase.solicitud.ListSolicitudesPendientesUseCase;
import co.com.crediya.solicitudes.usecase.solicitud.SolicitudUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, SolicitudHandler.class})
@WebFluxTest(excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SolicitudUseCase solicitudUseCase;

    @MockBean
    private ListSolicitudesPendientesUseCase listSolicitudesPendientesUseCase;

    @Test
    void testRegistrarSolicitud() {
        var request = new RegistrarSolicitudRequest();
        request.setDocumentoIdentidad("12345");
        request.setEmail("test@test.com");
        request.setMonto(BigDecimal.TEN);
        request.setPlazo(12);
        request.setIdTipoPrestamo(1);

        var solicitud = Solicitud.builder()
                .idSolicitud(1)
                .documentoIdentidad("12345")
                .email("test@test.com")
                .monto(BigDecimal.TEN)
                .plazo(12)
                .build();

        when(solicitudUseCase.registrar(any())).thenReturn(Mono.just(solicitud));

        webTestClient.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.idSolicitud").isEqualTo(1)
                .jsonPath("$.email").isEqualTo("test@test.com");
    }

    @Test
    void testListarSolicitudes() {
        when(listSolicitudesPendientesUseCase.execute(any(), any())).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/solicitud")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }
}
