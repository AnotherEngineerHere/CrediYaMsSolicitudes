package co.com.crediya.solicitudes.usecase.solicitud;

import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitud;
import co.com.crediya.solicitudes.model.solicitud.PageQuery;
import co.com.crediya.solicitudes.model.solicitud.PagedResult;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class ListSolicitudesPendientesUseCase {

    private final SolicitudRepository repository;



    public Mono<PagedResult<Solicitud>> execute(FiltroSolicitud filtro, PageQuery page) {
        int safeSize = Math.min(Math.max(page.getSize(), 1), 200);
        PageQuery safe = new PageQuery(
                Math.max(page.getPage(), 0),
                safeSize,
                page.getSortBy() == null ? "id_solicitud" : page.getSortBy(),
                page.isAsc()
        );
        return repository.listarPendientes(filtro, safe)
                .doOnSubscribe(s -> log.trace("Listar pendientes filtro={} page={}", filtro, safe))
                .doOnSuccess(p -> log.debug("OK {} items (total={})", p.getItems().size(), p.getTotal()))
                .doOnError(e -> log.error("Error listando pendientes: {}", e.getMessage(), e));
    }
}