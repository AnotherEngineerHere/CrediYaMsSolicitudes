package co.com.crediya.solicitudes.usecase.solicitud;

import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitud;
import co.com.crediya.solicitudes.model.solicitud.PageQuery;
import co.com.crediya.solicitudes.model.solicitud.PagedResult;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ListSolicitudesPendientesUseCaseTest {

    @Mock
    private SolicitudRepository repository;

    @InjectMocks
    private ListSolicitudesPendientesUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_withValidFilterAndPage_shouldReturnPagedResult() {
        var filtro = FiltroSolicitud.builder().build();
        var page = new PageQuery(0, 10, "id", true);
        var pagedResult = new PagedResult<>(Collections.<Solicitud>emptyList(), 0, 0, 10);

        when(repository.listarPendientes(any(FiltroSolicitud.class), any(PageQuery.class)))
                .thenReturn(Mono.just(pagedResult));

        StepVerifier.create(useCase.execute(filtro, page))
                .expectNext(pagedResult)
                .verifyComplete();
    }
}
