package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.r2dbc.entity.EstadoEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface EstadoReactiveRepository extends ReactiveCrudRepository<EstadoEntity, Long> {}

