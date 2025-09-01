package co.com.crediya.solicitudes.usecase.estado;

import co.com.crediya.solicitudes.model.enums.EstadoTipo;
import co.com.crediya.solicitudes.model.estados.gateways.EstadosRepository;
import co.com.crediya.solicitudes.model.excepciones.DomainException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class EstadoUseCase {

    private final EstadosRepository estadosRepository;

    /** Verifica si existe el estado por id. */
    public Mono<Boolean> existsById(Long idEstado) {
        if (idEstado == null) {
            return Mono.error(new DomainException("El id del estado es obligatorio") {});
        }
        return estadosRepository.existsEstadoById(idEstado)
                .doOnNext(exists ->
                        System.out.println("[EstadoUseCase] existsById(" + idEstado + ") -> " + exists)
                );
    }

    /** Valida que el estado CREADA (id=1) esté configurado en BD. */
    public Mono<Void> validarExisteEstadoCreada() {
        long idCreada = EstadoTipo.CREADA.getId();
        return estadosRepository.existsEstadoById(idCreada)
                .flatMap(exists -> exists
                        ? Mono.<Void>empty()
                        : Mono.error(new DomainException("El estado CREADA (id=" + idCreada + ") no está configurado en la base de datos") {}))
                .doOnSuccess(v ->
                        System.out.println("[EstadoUseCase] Estado CREADA verificado")
                )
                .doOnError(e ->
                        System.err.println("[EstadoUseCase] Error verificando estado CREADA: " + e.getMessage())
                );
    }
}
