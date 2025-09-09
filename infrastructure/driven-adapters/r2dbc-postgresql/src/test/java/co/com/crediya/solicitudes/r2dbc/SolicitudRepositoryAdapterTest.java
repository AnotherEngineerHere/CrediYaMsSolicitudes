package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitud;
import co.com.crediya.solicitudes.model.solicitud.PageQuery;
import co.com.crediya.solicitudes.model.solicitud.PagedResult;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.r2dbc.entity.SolicitudEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class SolicitudRepositoryAdapterTest {

    @Mock
    private SolicitudReactiveRepository repository;

    @InjectMocks
    private SolicitudRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_withValidSolicitud_shouldSaveAndReturnSolicitud() {
        var solicitud = Solicitud.builder()
                .documentoIdentidad("12345")
                .email("test@test.com")
                .monto(BigDecimal.TEN)
                .plazo(12)
                .tipoPrestamo(TipoPrestamo.builder().idTipoPrestamo(1).build())
                .build();

        var entity = SolicitudEntity.builder()
                .id(1L)
                .email("test@test.com")
                .monto(BigDecimal.TEN)
                .plazo(12)
                .idTipoPrestamo(1L)
                .build();

        when(repository.save(any(SolicitudEntity.class))).thenReturn(Mono.just(entity));

        StepVerifier.create(adapter.save(solicitud))
                .assertNext(saved -> {
                    assertNotNull(saved);
                    assertEquals(1, saved.getIdSolicitud());
                })
                .verifyComplete();
    }

    @Test
    void save_withNullSolicitud_shouldReturnError() {
        StepVerifier.create(adapter.save(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void listarPendientes_shouldReturnPagedResult() {
        var filtro = FiltroSolicitud.builder().build();
        var page = new PageQuery(0, 10, "id", true);
        var pagedResult = new PagedResult<>(Collections.<Solicitud>emptyList(), 0, 0, 10);

        when(repository.listarPendientes(any(FiltroSolicitud.class), any(PageQuery.class)))
                .thenReturn(Mono.just(pagedResult));

        StepVerifier.create(adapter.listarPendientes(filtro, page))
                .expectNext(pagedResult)
                .verifyComplete();
    }
}
