package co.com.crediya.solicitudes.usecase.solicitud;

import co.com.crediya.solicitudes.model.excepciones.DocumentoIdentidadInvalidoException;
import co.com.crediya.solicitudes.model.excepciones.DomainException;
import co.com.crediya.solicitudes.model.excepciones.EmailInvalidoException;
import co.com.crediya.solicitudes.model.excepciones.PlazoInvalidoException;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.usecase.estado.EstadoUseCase;
import co.com.crediya.solicitudes.usecase.tipoprestamo.TipoPrestamoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class SolicitudUseCaseTest {

    @Mock
    private SolicitudRepository solicitudRepository;
    @Mock
    private TipoPrestamoUseCase tipoPrestamoUseCase;
    @Mock
    private EstadoUseCase estadoUseCase;

    @InjectMocks
    private SolicitudUseCase solicitudUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrar_withValidCommand_shouldRegisterSolicitud() {
        var cmd = ComandoRegistrarSolicitud.builder()
                .documentoIdentidad("12345")
                .email("test@test.com")
                .monto(BigDecimal.TEN)
                .plazo(12)
                .idTipoPrestamo(1)
                .build();

        when(tipoPrestamoUseCase.validarQueExista(anyLong())).thenReturn(Mono.empty());
        when(estadoUseCase.validarExisteEstadoCreada()).thenReturn(Mono.empty());
        when(solicitudRepository.save(any(Solicitud.class))).thenReturn(Mono.just(Solicitud.builder().build()));

        StepVerifier.create(solicitudUseCase.registrar(cmd))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void registrar_withNullCommand_shouldThrowException() {
        assertThrows(DomainException.class, () -> solicitudUseCase.registrar(null));
    }

    @Test
    void registrar_withInvalidDocumento_shouldThrowException() {
        var cmd = ComandoRegistrarSolicitud.builder().documentoIdentidad("").build();
        assertThrows(DocumentoIdentidadInvalidoException.class, () -> solicitudUseCase.registrar(cmd));
    }

    @Test
    void registrar_withInvalidEmail_shouldThrowException() {
        var cmd = ComandoRegistrarSolicitud.builder().documentoIdentidad("123").email("invalid").build();
        assertThrows(EmailInvalidoException.class, () -> solicitudUseCase.registrar(cmd));
    }

    @Test
    void registrar_withInvalidMonto_shouldThrowException() {
        var cmd = ComandoRegistrarSolicitud.builder().documentoIdentidad("123").email("a@b.c").monto(BigDecimal.ZERO).build();
        assertThrows(DomainException.class, () -> solicitudUseCase.registrar(cmd));
    }

    @Test
    void registrar_withInvalidPlazo_shouldThrowException() {
        var cmd = ComandoRegistrarSolicitud.builder().documentoIdentidad("123").email("a@b.c").monto(BigDecimal.ONE).plazo(0).build();
        assertThrows(PlazoInvalidoException.class, () -> solicitudUseCase.registrar(cmd));
    }

    @Test
    void registrar_withNullTipoPrestamo_shouldThrowException() {
        var cmd = ComandoRegistrarSolicitud.builder().documentoIdentidad("123").email("a@b.c").monto(BigDecimal.ONE).plazo(1).idTipoPrestamo(null).build();
        assertThrows(DomainException.class, () -> solicitudUseCase.registrar(cmd));
    }
}
