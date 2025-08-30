package co.com.crediya.solicitudes.model.estados.gateways;

import reactor.core.publisher.Mono;

public interface EstadosRepository {
    Mono<Boolean> existsEstadoById(Long id);
}
