package co.com.crediya.solicitudes.r2dbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class EstadosRepositoryAdapterTest {

    @Mock
    private EstadoReactiveRepository repository;

    @InjectMocks
    private EstadosRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void existsEstadoById_whenExists_shouldReturnTrue() {
        when(repository.existsById(anyLong())).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsEstadoById(1L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsEstadoById_whenNotExists_shouldReturnFalse() {
        when(repository.existsById(anyLong())).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.existsEstadoById(1L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void existsEstadoById_withNullId_shouldReturnError() {
        StepVerifier.create(adapter.existsEstadoById(null))
                .expectError()
                .verify();
    }
}
