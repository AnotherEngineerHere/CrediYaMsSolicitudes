package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.*;
import co.com.crediya.solicitudes.model.excepciones.*;
import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitud;
import co.com.crediya.solicitudes.model.solicitud.PageQuery;
import co.com.crediya.solicitudes.model.solicitud.SolicitudRevision;
import co.com.crediya.solicitudes.usecase.solicitud.ListSolicitudesPendientesUseCase;
import co.com.crediya.solicitudes.usecase.solicitud.ListSolicitudesRevisionUseCase;
import co.com.crediya.solicitudes.usecase.solicitud.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

/**
 * Handler encargado de procesar las solicitudes HTTP relacionadas con la entidad Solicitud.
 * Implementado con el enfoque funcional de Spring WebFlux.
 *
 * Nota: Se removió el uso de TransactionalOperator y Validator para simplificar,
 * siguiendo el estilo del ejemplo de Autenticación.
 */
@Component
@RequiredArgsConstructor
public class SolicitudHandler {

    private final SolicitudUseCase solicitudUseCase;
    private final ListSolicitudesPendientesUseCase useCase;
    private final ListSolicitudesRevisionUseCase listSolicitudesRevisionUseCase;

    private static final Logger log = LoggerFactory.getLogger(SolicitudHandler.class);
    /**
     * Maneja la creación de una nueva solicitud de crédito.
     *
     * @param request cuerpo de la petición con datos de la solicitud.
     * @return {@link ServerResponse} con estado 201 Created y datos de la solicitud creada.
     */
    public Mono<ServerResponse> registrar(ServerRequest request) {
       log.info("Solicitud para crear una nueva solicitud recibida");

        return request.bodyToMono(RegistrarSolicitudRequest.class)
                .doOnNext(dto -> System.out.println("Payload recibido: " + dto))
                .map(RegistrarSolicitudRequest::toCommand)
                .doOnNext(cmd -> System.out.println("Comando mapeado: " + cmd.getEmail()))
                .flatMap(solicitudUseCase::registrar)
                .doOnSuccess(saved -> System.out.println("Solicitud guardada exitosamente con id: " + saved.getIdSolicitud()))
                .flatMap(saved -> {
                    URI location = URI.create("/api/v1/solicitud/" + saved.getIdSolicitud());
                    return ServerResponse.created(location)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(SolicitudResponse.fromDomain(saved));
                })
                // --- Mapeo de excepciones de dominio a respuestas HTTP controladas ---
                .onErrorResume(TipoPrestamoNoEncontradoException.class, ex ->
                        ServerResponse.status(422)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(error("TIPO_PRESTAMO_NO_ENCONTRADO", ex.getMessage()))
                )
                .onErrorResume(DocumentoIdentidadInvalidoException.class, ex ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(error("DOCUMENTO_INVALIDO", ex.getMessage()))
                )
                .onErrorResume(EmailInvalidoException.class, ex ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(error("EMAIL_INVALIDO", ex.getMessage()))
                )
                .onErrorResume(PlazoInvalidoException.class, ex ->
                        ServerResponse.badRequest()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(error("PLAZO_INVALIDO", ex.getMessage()))
                )
                .onErrorResume(DomainException.class, ex ->
                        ServerResponse.status(422)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(error("DOMAIN_ERROR", ex.getMessage()))
                )
                // --- Fallback genérico (500) ---
                .onErrorResume(ex -> {
                    System.out.println("Error al registrar solicitud: " + ex.getMessage());
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(error("ERROR_INTERNO",
                                    "Ocurrió un error inesperado. Contacte con el administrador."));
                });
    }

    // Helper para respuestas de error homogéneas
    private Map<String, Object> error(String code, String message) {
        return Map.of(
                "code", code,
                "message", message
        );
    }


    public Mono<ServerResponse> listar(ServerRequest req) {
        int page = qpi(req, "page", 0);
        int size = qpi(req, "size", 20);
        String sort = req.queryParam("sort").orElse("id_solicitud");
        boolean asc = !"desc".equalsIgnoreCase(req.queryParam("dir").orElse("asc"));

        var filtro = FiltroSolicitud.builder()
                .email(req.queryParam("email").orElse(null))
                .nombre(req.queryParam("nombre").orElse(null))
                .tipoPrestamo(req.queryParam("tipo_prestamo").orElse(null))
                .minMonto(req.queryParam("min_monto").map(BigDecimal::new).orElse(null))
                .maxMonto(req.queryParam("max_monto").map(BigDecimal::new).orElse(null))
                .build();

        var pageQ = new PageQuery(page, size, sort, asc);

        return useCase.execute(filtro, pageQ)
                .map(p -> PagedResponseDTO.<SolicitudItemDTO>builder()
                        .items(p.getItems().stream().map(SolicitudMapper::toDto).toList())
                        .total(p.getTotal()).page(p.getPage()).size(p.getSize()).build())
                .flatMap(body -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(body))
                .onErrorResume(IllegalArgumentException.class,
                        e -> ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ErrorResponse.of("BAD_REQUEST", e.getMessage())))
                .onErrorResume(e -> {
                    log.error("Error no controlado en listado: {}", e.getMessage(), e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ErrorResponse.of("ERROR_INTERNO",
                                    "Ocurrió un error inesperado. Contacte con el administrador."));
                });
    }

    private static int qpi(ServerRequest r, String k, int d) {
        try { return Integer.parseInt(r.queryParam(k).orElse(String.valueOf(d))); }
        catch (Exception e) { return d; }
    }

    public Mono<ServerResponse> listarRevision(ServerRequest req) {
        int page = qpi(req, "page", 0);
        int size = qpi(req, "size", 20);
        String sort = req.queryParam("sort").orElse("id_solicitud");
        boolean asc = !"desc".equalsIgnoreCase(req.queryParam("dir").orElse("asc"));

        var filtro = FiltroSolicitud.builder()
                .email(req.queryParam("email").orElse(null))
                .nombre(req.queryParam("nombre").orElse(null))
                .tipoPrestamo(req.queryParam("tipo_prestamo").orElse(null))
                .minMonto(req.queryParam("min_monto").map(BigDecimal::new).orElse(null))
                .maxMonto(req.queryParam("max_monto").map(BigDecimal::new).orElse(null))
                .build();

        var pageQ = new PageQuery(page, size, sort, asc);

        return listSolicitudesRevisionUseCase.execute(filtro, pageQ)
                .map(p -> PagedResponseDTO.<SolicitudRevisionDTO>builder()
                        .items(p.getItems().stream().map(SolicitudRevisionMapper::toDto).toList())
                        .total(p.getTotal()).page(p.getPage()).size(p.getSize()).build())
                .flatMap(body -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(body))
                .onErrorResume(IllegalArgumentException.class,
                        e -> ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(ErrorResponse.of("BAD_REQUEST", e.getMessage())))
                .onErrorResume(e -> {
                    log.error("Error no controlado en listado de revision: {}", e.getMessage(), e);
                    return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(ErrorResponse.of("ERROR_INTERNO",
                                    "Ocurrió un error inesperado. Contacte con el administrador."));
                });
    }
}
