package co.com.crediya.solicitudes.usecase.tipoprestamo;

import co.com.crediya.solicitudes.model.excepciones.DomainException;
import co.com.crediya.solicitudes.model.excepciones.TipoPrestamoNoEncontradoException;
import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class TipoPrestamoUseCaseTest {

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @InjectMocks
    private TipoPrestamoUseCase tipoPrestamoUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void existsById_whenExists_shouldReturnTrue() {
        when(tipoPrestamoRepository.existsTipoPrestamoById(anyLong())).thenReturn(Mono.just(true));

        StepVerifier.create(tipoPrestamoUseCase.existsById(1L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsById_whenNotExists_shouldReturnFalse() {
        when(tipoPrestamoRepository.existsTipoPrestamoById(anyLong())).thenReturn(Mono.just(false));

        StepVerifier.create(tipoPrestamoUseCase.existsById(1L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void existsById_withNullId_shouldReturnError() {
        StepVerifier.create(tipoPrestamoUseCase.existsById(null))
                .expectError(DomainException.class)
                .verify();
    }

    @Test
    void validarQueExista_whenExists_shouldComplete() {
        when(tipoPrestamoRepository.existsTipoPrestamoById(anyLong())).thenReturn(Mono.just(true));

        StepVerifier.create(tipoPrestamoUseCase.validarQueExista(1L))
                .verifyComplete();
    }

    @Test
    void validarQueExista_whenNotExists_shouldReturnError() {
        when(tipoPrestamoRepository.existsTipoPrestamoById(anyLong())).thenReturn(Mono.just(false));

        StepVerifier.create(tipoPrestamoUseCase.validarQueExista(1L))
                .expectError(TipoPrestamoNoEncontradoException.class)
                .verify();
    }

    @Test
    void validarQueExista_withNullId_shouldReturnError() {
        StepVerifier.create(tipoPrestamoUseCase.validarQueExista(null))
                .expectError(DomainException.class)
                .verify();
    }
}
