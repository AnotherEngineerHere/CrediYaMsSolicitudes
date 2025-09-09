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

class TipoPrestamoRepositoryAdapterTest {

    @Mock
    private TipoPrestamoReactiveRepository repository;

    @InjectMocks
    private TipoPrestamoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void existsTipoPrestamoById_whenExists_shouldReturnTrue() {
        when(repository.existsById(anyLong())).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsTipoPrestamoById(1L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsTipoPrestamoById_whenNotExists_shouldReturnFalse() {
        when(repository.existsById(anyLong())).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.existsTipoPrestamoById(1L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void existsTipoPrestamoById_withNullId_shouldReturnError() {
        StepVerifier.create(adapter.existsTipoPrestamoById(null))
                .expectError()
                .verify();
    }
}
