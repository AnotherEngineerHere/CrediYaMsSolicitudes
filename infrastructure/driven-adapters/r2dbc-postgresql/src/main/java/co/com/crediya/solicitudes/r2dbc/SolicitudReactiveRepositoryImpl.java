package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.model.enums.EstadoTipo;
import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitud;
import co.com.crediya.solicitudes.model.solicitud.PageQuery;
import co.com.crediya.solicitudes.model.solicitud.PagedResult;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.r2dbc.mapper.SolicitudMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class SolicitudReactiveRepositoryImpl implements SolicitudReactiveRepositoryCustom {

    private final R2dbcEntityTemplate template;
    private final SolicitudMapper mapper;

    @Override
    public Mono<PagedResult<Solicitud>> listarPendientes(FiltroSolicitud filtro, PageQuery page) {
        var criteria = Criteria.where("id_estado").in(
                EstadoTipo.EN_REVISION.getId(),
                EstadoTipo.RECHAZADA.getId(),
                EstadoTipo.REVISION_MANUAL.getId()
        );

        if (filtro.getEmail() != null && !filtro.getEmail().isEmpty()) {
            criteria = criteria.and("email").is(filtro.getEmail());
        }
        if (filtro.getMinMonto() != null) {
            criteria = criteria.and("monto").greaterThanOrEquals(filtro.getMinMonto());
        }
        if (filtro.getMaxMonto() != null) {
            criteria = criteria.and("monto").lessThanOrEquals(filtro.getMaxMonto());
        }

        var query = Query.query(criteria)
                .limit(page.getSize())
                .offset((long) page.getPage() * page.getSize());

        var countQuery = Query.query(criteria);

        return template.select(query, co.com.crediya.solicitudes.r2dbc.entity.SolicitudEntity.class)
                .map(mapper::toDomain)
                .collectList()
                .zipWith(template.count(countQuery, co.com.crediya.solicitudes.r2dbc.entity.SolicitudEntity.class))
                .map(tuple -> new PagedResult<>(tuple.getT1(), tuple.getT2(), page.getPage(), page.getSize()));
    }
}
