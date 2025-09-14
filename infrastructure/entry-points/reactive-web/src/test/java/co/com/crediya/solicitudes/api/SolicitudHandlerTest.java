package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.SolicitudRevisionDTO;
import co.com.crediya.solicitudes.api.dto.UserDataDTO;
import co.com.crediya.solicitudes.api.service.UserService;
import co.com.crediya.solicitudes.model.enums.EstadoTipo;
import co.com.crediya.solicitudes.model.estados.Estado;
import co.com.crediya.solicitudes.model.solicitud.*;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.usecase.solicitud.ListSolicitudesPendientesUseCase;
import co.com.crediya.solicitudes.usecase.solicitud.SolicitudUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudHandlerTest {

    @Mock
    private SolicitudUseCase solicitudUseCase;

    @Mock
    private ListSolicitudesPendientesUseCase listSolicitudesPendientesUseCase;

    @Mock
    private UserService userService;

    @InjectMocks
    private SolicitudHandler solicitudHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        RouterRest routerRest = new RouterRest();
        RouterFunction<ServerResponse> route = routerRest.routerFunction(solicitudHandler);
        webTestClient = WebTestClient.bindToRouterFunction(route).build();
    }

    @Test
    void listarParaRevision_shouldReturnEnrichedDataFromAuthenticationService() {
        // Given
        var solicitud = createTestSolicitud();
        var pagedResult = createPagedResult(List.of(solicitud));
        var userData = new UserDataDTO("test@example.com", "Juan Pérez", 5000000L);

        when(listSolicitudesPendientesUseCase.execute(any(FiltroSolicitud.class), any(PageQuery.class)))
                .thenReturn(Mono.just(pagedResult));
        when(userService.getUserData("test@example.com"))
                .thenReturn(Mono.just(userData));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/solicitud")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.items[0].email").isEqualTo("test@example.com")
                .jsonPath("$.items[0].nombre").isEqualTo("Juan Pérez")
                .jsonPath("$.items[0].salarioBase").isEqualTo(5000000)
                .jsonPath("$.items[0].monto").isEqualTo(10000.00)
                .jsonPath("$.items[0].plazo").isEqualTo(12)
                .jsonPath("$.items[0].estadoSolicitud").isEqualTo("EN_REVISION");
    }

    @Test
    void listarParaRevision_shouldHandleUserNotFound() {
        // Given
        var solicitud = createTestSolicitud();
        var pagedResult = createPagedResult(List.of(solicitud));

        when(listSolicitudesPendientesUseCase.execute(any(FiltroSolicitud.class), any(PageQuery.class)))
                .thenReturn(Mono.just(pagedResult));
        when(userService.getUserData("test@example.com"))
                .thenReturn(Mono.empty());

        // When & Then
        webTestClient.get()
                .uri("/api/v1/solicitud")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.items[0].email").isEqualTo("test@example.com")
                .jsonPath("$.items[0].nombre").isEqualTo("Usuario no encontrado")
                .jsonPath("$.items[0].salarioBase").isEqualTo(0);
    }

    @Test
    void listarParaRevision_shouldFilterOnlyRequiredStates() {
        // Given
        var solicitudEnRevision = createTestSolicitudWithState(EstadoTipo.EN_REVISION);
        var solicitudRechazada = createTestSolicitudWithState(EstadoTipo.RECHAZADA);
        var solicitudRevisionManual = createTestSolicitudWithState(EstadoTipo.REVISION_MANUAL);
        var solicitudAprobada = createTestSolicitudWithState(EstadoTipo.APROBADA);

        var allSolicitudes = List.of(solicitudEnRevision, solicitudRechazada,
                                   solicitudRevisionManual, solicitudAprobada);
        var pagedResult = createPagedResult(allSolicitudes);

        when(listSolicitudesPendientesUseCase.execute(any(FiltroSolicitud.class), any(PageQuery.class)))
                .thenReturn(Mono.just(pagedResult));
        when(userService.getUserData(any(String.class)))
                .thenReturn(Mono.just(new UserDataDTO("test@example.com", "Test User", 3000000L)));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/solicitud")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.items.length()").isEqualTo(3) // Only 3 items should be returned
                .jsonPath("$.items[?(@.estadoSolicitud == 'EN_REVISION')]").exists()
                .jsonPath("$.items[?(@.estadoSolicitud == 'RECHAZADA')]").exists()
                .jsonPath("$.items[?(@.estadoSolicitud == 'REVISION_MANUAL')]").exists();
    }

    @Test
    void listarParaRevision_shouldHandleInvalidParameters() {
        // When & Then
        webTestClient.get()
                .uri("/api/v1/solicitud?page=invalid&size=invalid")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo("PARAMETROS_INVALIDOS")
                .jsonPath("$.message").isEqualTo("Los parámetros 'page' y 'size' deben ser números enteros.");
    }

    @Test
    void listarParaRevision_shouldCalculateMonthlyPaymentCorrectly() {
        // Given
        var solicitud = createTestSolicitud();
        var pagedResult = createPagedResult(List.of(solicitud));
        var userData = new UserDataDTO("test@example.com", "Test User", 4000000L);

        when(listSolicitudesPendientesUseCase.execute(any(FiltroSolicitud.class), any(PageQuery.class)))
                .thenReturn(Mono.just(pagedResult));
        when(userService.getUserData("test@example.com"))
                .thenReturn(Mono.just(userData));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/solicitud")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.items[0].montoMensualSolicitud").isEqualTo(833.33); // 10000 / 12 = 833.33
    }

    private Solicitud createTestSolicitud() {
        return createTestSolicitudWithState(EstadoTipo.EN_REVISION);
    }

    private Solicitud createTestSolicitudWithState(EstadoTipo estadoTipo) {
        Estado estado = Estado.builder()
                .tipo(estadoTipo)
                .build();

        TipoPrestamo tipoPrestamo = TipoPrestamo.builder()
                .nombre("Préstamo Personal")
                .build();

        return Solicitud.builder()
                .idSolicitud(1)
                .email("test@example.com")
                .monto(BigDecimal.valueOf(10000.00))
                .plazo(12)
                .estado(estado)
                .tipoPrestamo(tipoPrestamo)
                .build();
    }

    private PagedResult<Solicitud> createPagedResult(List<Solicitud> items) {
        return new PagedResult<>(items, items.size(), 0, 10);
    }
}