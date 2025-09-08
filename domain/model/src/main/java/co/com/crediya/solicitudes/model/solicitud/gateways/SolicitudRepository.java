package co.com.crediya.solicitudes.model.solicitud.gateways;

import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitud;
import co.com.crediya.solicitudes.model.solicitud.PageQuery;
import co.com.crediya.solicitudes.model.solicitud.PagedResult;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {

    Mono<Solicitud> save(Solicitud solicitud);

    Mono<PagedResult<Solicitud>> listarPendientes(FiltroSolicitud filtro, PageQuery page);
}
