package co.com.crediya.solicitudes.usecase.estado;

import co.com.crediya.solicitudes.model.estados.gateways.EstadosRepository;
import co.com.crediya.solicitudes.model.excepciones.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

class EstadoUseCaseTest {

    @Mock
    private EstadosRepository estadosRepository;

    @InjectMocks
    private EstadoUseCase estadoUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void existsById_whenExists_shouldReturnTrue() {
        when(estadosRepository.existsEstadoById(anyLong())).thenReturn(Mono.just(true));

        StepVerifier.create(estadoUseCase.existsById(1L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsById_whenNotExists_shouldReturnFalse() {
        when(estadosRepository.existsEstadoById(anyLong())).thenReturn(Mono.just(false));

        StepVerifier.create(estadoUseCase.existsById(1L))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void existsById_withNullId_shouldReturnError() {
        StepVerifier.create(estadoUseCase.existsById(null))
                .expectError(DomainException.class)
                .verify();
    }

    @Test
    void validarExisteEstadoCreada_whenExists_shouldComplete() {
        when(estadosRepository.existsEstadoById(anyLong())).thenReturn(Mono.just(true));

        StepVerifier.create(estadoUseCase.validarExisteEstadoCreada())
                .verifyComplete();
    }

    @Test
    void validarExisteEstadoCreada_whenNotExists_shouldReturnError() {
        when(estadosRepository.existsEstadoById(anyLong())).thenReturn(Mono.just(false));

        StepVerifier.create(estadoUseCase.validarExisteEstadoCreada())
                .expectError(DomainException.class)
                .verify();
    }
}
