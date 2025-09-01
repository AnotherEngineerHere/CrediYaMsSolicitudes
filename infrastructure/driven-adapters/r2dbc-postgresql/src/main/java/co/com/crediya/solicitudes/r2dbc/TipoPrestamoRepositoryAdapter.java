package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class TipoPrestamoRepositoryAdapter implements TipoPrestamoRepository {

    private static final Logger log = LoggerFactory.getLogger(TipoPrestamoRepositoryAdapter.class);
    private final TipoPrestamoReactiveRepository repository;

    public TipoPrestamoRepositoryAdapter(TipoPrestamoReactiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Boolean> existsTipoPrestamoById(Long id) {
        if (id == null) {
            return Mono.error(new IllegalArgumentException("El id de tipo de préstamo no puede ser nulo"));
        }
        return repository.existsById(id)
                .doOnNext(exists -> log.debug("existsTipoPrestamoById({}) -> {}", id, exists));
    }
}
