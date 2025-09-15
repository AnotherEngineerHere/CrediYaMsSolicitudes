package co.com.crediya.solicitudes.usecase.solicitud;

import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitud;
import co.com.crediya.solicitudes.model.solicitud.PageQuery;
import co.com.crediya.solicitudes.model.solicitud.PagedResult;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import reactor.core.publisher.Mono;


public record ListSolicitudesPendientesUseCase(SolicitudRepository repository) {

    public Mono<PagedResult<Solicitud>> execute(FiltroSolicitud filtro, PageQuery page) {
        int safeSize = Math.min(Math.max(page.getSize(), 1), 200);
        PageQuery safe = new PageQuery(
                Math.max(page.getPage(), 0),
                safeSize,
                page.getSortBy() == null ? "id_solicitud" : page.getSortBy(),
                page.isAsc()
        );
        return repository.listarPendientes(filtro, safe)
                .doOnSubscribe(s -> System.out.println(
                        "Listar pendientes filtro=" + filtro + " page=" + safe))
                .doOnSuccess(p -> {
                    System.out.println("OK " + p.getItems().size() + " items (total=" + p.getTotal() + ")");
                    p.getItems().forEach(item -> System.out.println("Solicitud: " + item));
                })
                .doOnError(e -> System.out.println(
                        "Error listando pendientes: " + e.getMessage()));
    }
}