package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.model.estados.gateways.EstadosRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class EstadosRepositoryAdapter implements EstadosRepository {

    private static final Logger log = LoggerFactory.getLogger(EstadosRepositoryAdapter.class);
    private final EstadoReactiveRepository repository;

    public EstadosRepositoryAdapter(EstadoReactiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Boolean> existsEstadoById(Long id) {
        if (id == null) {
            return Mono.error(new IllegalArgumentException("El id de estado no puede ser nulo"));
        }
        return repository.existsById(id)
                .doOnNext(exists -> log.debug("existsEstadoById({}) -> {}", id, exists));
    }
}
