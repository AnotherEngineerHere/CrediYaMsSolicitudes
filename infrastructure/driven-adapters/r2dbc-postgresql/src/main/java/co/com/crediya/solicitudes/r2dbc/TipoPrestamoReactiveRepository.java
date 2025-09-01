package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.r2dbc.entity.TipoPrestamoEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TipoPrestamoReactiveRepository extends ReactiveCrudRepository<TipoPrestamoEntity, Long> {}

