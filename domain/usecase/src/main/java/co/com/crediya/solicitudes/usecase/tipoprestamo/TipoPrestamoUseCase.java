package co.com.crediya.solicitudes.usecase.tipoprestamo;

import co.com.crediya.solicitudes.model.excepciones.DomainException;
import co.com.crediya.solicitudes.model.excepciones.TipoPrestamoNoEncontradoException;
import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TipoPrestamoUseCase {

    private final TipoPrestamoRepository tipoPrestamoRepository;

    /** Verifica si existe el tipo de préstamo por id. */
    public Mono<Boolean> existsById(Long idTipoPrestamo) {
        if (idTipoPrestamo == null) {
            return Mono.error(new DomainException("El idTipoPrestamo es obligatorio") {});
        }
        return tipoPrestamoRepository.existsTipoPrestamoById(idTipoPrestamo)
                .doOnNext(exists ->
                        System.out.println("[TipoPrestamoUseCase] existsById(" + idTipoPrestamo + ") -> " + exists)
                );
    }

    /** Lanza excepción si el tipo de préstamo NO existe. */
    public Mono<Void> validarQueExista(Long idTipoPrestamo) {
        if (idTipoPrestamo == null) {
            return Mono.error(new DomainException("El idTipoPrestamo es obligatorio") {});
        }
        return tipoPrestamoRepository.existsTipoPrestamoById(idTipoPrestamo)
                .flatMap(exists -> exists
                        ? Mono.<Void>empty()
                        : Mono.error(new TipoPrestamoNoEncontradoException(""+idTipoPrestamo)))
                .doOnSuccess(v ->
                        System.out.println("[TipoPrestamoUseCase] TipoPrestamo " + idTipoPrestamo + " verificado")
                )
                .doOnError(e ->
                        System.err.println("[TipoPrestamoUseCase] Error verificando TipoPrestamo " + idTipoPrestamo + ": " + e.getMessage())
                );
    }
}
