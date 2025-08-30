package co.com.crediya.solicitudes.model.tipoprestamo.gateways;

import reactor.core.publisher.Mono;

public interface TipoPrestamoRepository {
    Mono<Boolean> existsTipoPrestamoById(Long id);
}
