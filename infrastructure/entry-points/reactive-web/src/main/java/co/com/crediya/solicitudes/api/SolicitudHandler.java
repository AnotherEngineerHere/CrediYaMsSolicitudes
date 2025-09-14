package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.*;
import co.com.crediya.solicitudes.api.service.UserService;
import co.com.crediya.solicitudes.model.enums.EstadoTipo;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Handler encargado de procesar las solicitudes HTTP relacionadas con la entidad Solicitud.
 * Implementado con el enfoque funcional de Spring WebFlux.
 *
 * Esta clase maneja las operaciones CRUD básicas para solicitudes de crédito,
 * incluyendo registro de nuevas solicitudes y consulta de solicitudes pendientes.
 *
 * @author CrediYa Development Team
 * @version 1.0
 * @since 2024
 *
 * Nota: Se removió el uso de TransactionalOperator y Validator para simplificar,
 * siguiendo el estilo del ejemplo de Autenticación.
 */
@Component
@RequiredArgsConstructor
public class SolicitudHandler {

    private final SolicitudUseCase solicitudUseCase;
    private final ListSolicitudesPendientesUseCase useCase;
    private final UserService userService;
    //private final ListSolicitudesRevisionUseCase listSolicitudesRevisionUseCase;

    private static final Logger log = LoggerFactory.getLogger(SolicitudHandler.class);
    /**
     * Maneja la creación de una nueva solicitud de crédito.
     *
     * Este método procesa la solicitud HTTP POST para registrar una nueva solicitud de crédito.
     * Valida los datos de entrada, aplica las reglas de negocio y persiste la solicitud en la base de datos.
     *
     * @param request la solicitud HTTP que contiene los datos de la nueva solicitud en formato JSON
     * @return {@link Mono<ServerResponse>} que emite una respuesta HTTP con:
     *         - Estado 201 (Created) y los datos de la solicitud creada si es exitosa
     *         - Estado 400 (Bad Request) si los datos de entrada son inválidos
     *         - Estado 422 (Unprocessable Entity) si hay errores de validación de negocio
     *         - Estado 500 (Internal Server Error) si ocurre un error interno
     *
     * @throws IllegalArgumentException si los parámetros de entrada son nulos o inválidos
     * @throws RuntimeException si ocurre un error durante el procesamiento de la solicitud
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

    /**
     * Maneja la consulta de solicitudes pendientes de revisión.
     *
     * Este método procesa la solicitud HTTP GET para obtener una lista paginada de solicitudes
     * de crédito que están en estado pendiente de revisión. Soporta parámetros de paginación
     * opcionales para controlar el tamaño de página y número de página.
     *
     * @param request la petición HTTP que puede contener parámetros de consulta:
     *                - page: número de página (0-based, por defecto 0)
     *                - size: tamaño de página (por defecto 10)
     * @return {@link Mono<ServerResponse>} que emite una respuesta HTTP con:
     *         - Estado 200 (OK) y lista paginada de solicitudes pendientes si es exitosa
     *         - Estado 400 (Bad Request) si los parámetros de paginación son inválidos
     *         - Estado 500 (Internal Server Error) si ocurre un error interno
     *
     * @throws NumberFormatException si los parámetros page o size no son números válidos
     * @throws IllegalArgumentException si los parámetros de paginación son inválidos
     */
    public Mono<ServerResponse> listar(ServerRequest request) {
        log.info("Solicitud para listar solicitudes pendientes recibida");

        // Extraer parámetros de consulta opcionales
        String pageParam = request.queryParam("page").orElse("0");
        String sizeParam = request.queryParam("size").orElse("10");

        try {
            int page = Integer.parseInt(pageParam);
            int size = Integer.parseInt(sizeParam);

            // Crear filtro vacío por defecto (puedes expandir con más filtros)
            FiltroSolicitud filtro = FiltroSolicitud.builder().build();
            PageQuery pageQuery = new PageQuery(page, size, null, true);

            return useCase.execute(filtro, pageQuery)
                    .flatMap(pagedResult -> {
                        // Convertir a DTOs para respuesta
                        var items = pagedResult.getItems().stream()
                                .map(SolicitudItemDTO::fromDomain)
                                .toList();

                        var response = PagedResponseDTO.<SolicitudItemDTO>builder()
                                .items(items)
                                .page(pagedResult.getPage())
                                .size(pagedResult.getSize())
                                .total(pagedResult.getTotal())
                                .build();

                        return ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(response);
                    })
                    .onErrorResume(ex -> {
                        log.error("Error al listar solicitudes pendientes: {}", ex.getMessage());
                        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(error("ERROR_INTERNO",
                                        "Ocurrió un error al consultar las solicitudes pendientes."));
                    });
        } catch (NumberFormatException ex) {
            return ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(error("PARAMETROS_INVALIDOS",
                            "Los parámetros 'page' y 'size' deben ser números enteros."));
        }
    }

    /**
     * Maneja la consulta de solicitudes que requieren revisión manual por parte del asesor.
     * Este endpoint está restringido únicamente para usuarios con rol ASESOR.
     *
     * @param request la petición HTTP con parámetros de consulta opcionales
     * @return {@link Mono<ServerResponse>} con lista paginada de solicitudes para revisión
     */
    public Mono<ServerResponse> listarParaRevision(ServerRequest request) {
        log.info("Solicitud para listar aplicaciones que requieren revisión manual");

        // Extraer parámetros de consulta opcionales
        String pageParam = request.queryParam("page").orElse("0");
        String sizeParam = request.queryParam("size").orElse("10");

        try {
            int page = Integer.parseInt(pageParam);
            int size = Integer.parseInt(sizeParam);

            // Crear filtro vacío (por ahora, filtraremos por estado en el handler)
            FiltroSolicitud filtro = FiltroSolicitud.builder().build();
            PageQuery pageQuery = new PageQuery(page, size, null, true);

            return useCase.execute(filtro, pageQuery)
                    .flatMap(pagedResult -> {
                        // Filtrar solicitudes que requieren revisión manual
                        List<co.com.crediya.solicitudes.model.solicitud.Solicitud> filteredSolicitudes = pagedResult.getItems().stream()
                                .filter(solicitud -> {
                                    if (solicitud.getEstado() == null || solicitud.getEstado().getTipo() == null) {
                                        return false;
                                    }
                                    EstadoTipo estadoTipo = solicitud.getEstado().getTipo();
                                    return estadoTipo == EstadoTipo.EN_REVISION ||
                                            estadoTipo == EstadoTipo.RECHAZADA ||
                                            estadoTipo == EstadoTipo.REVISION_MANUAL;
                                })
                                .toList();

                        // Procesar cada solicitud y enriquecer con datos de usuario
                        return Flux.fromIterable(filteredSolicitudes)
                                .flatMap(solicitud -> {
                                    // Obtener datos del usuario
                                    Mono<co.com.crediya.solicitudes.api.dto.UserDataDTO> userDataMono = userService.getUserData(solicitud.getEmail())
                                            .defaultIfEmpty(new co.com.crediya.solicitudes.api.dto.UserDataDTO(
                                                    solicitud.getEmail(), "Usuario no encontrado", 0L));

                                    return userDataMono.map(userData -> {
                                        // Calcular monto mensual aproximado (monto / plazo)
                                        BigDecimal montoMensual = solicitud.getMonto()
                                                .divide(BigDecimal.valueOf(solicitud.getPlazo()),
                                                        2, BigDecimal.ROUND_HALF_UP);

                                        return SolicitudRevisionDTO.builder()
                                                .monto(solicitud.getMonto())
                                                .plazo(solicitud.getPlazo())
                                                .email(solicitud.getEmail())
                                                .nombre(userData.nombre())
                                                .tipoPrestamo(solicitud.getTipoPrestamo() != null ?
                                                        solicitud.getTipoPrestamo().getNombre() : null)
                                                .tasaInteres(BigDecimal.valueOf(0.025)) // Tasa por defecto
                                                .estadoSolicitud(solicitud.getEstado() != null ?
                                                        solicitud.getEstado().getTipo().name() : null)
                                                .salarioBase(userData.salarioBase())
                                                .montoMensualSolicitud(montoMensual)
                                                .build();
                                    });
                                })
                                .collectList()
                                .map(filteredItems -> {
                                    var response = PagedResponseDTO.<SolicitudRevisionDTO>builder()
                                            .items(filteredItems)
                                            .total(filteredItems.size()) // TODO: Calcular total correcto con filtro
                                            .page(pagedResult.getPage())
                                            .size(pagedResult.getSize())
                                            .build();

                                    return ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(response);
                                });
                    })
                    .flatMap(response -> response)
                    .onErrorResume(ex -> {
                        log.error("Error al listar solicitudes para revisión: {}", ex.getMessage(), ex);
                        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(error("ERROR_INTERNO",
                                        "Ocurrió un error al consultar las solicitudes para revisión."));
                    });
        } catch (NumberFormatException ex) {
            return ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(error("PARAMETROS_INVALIDOS",
                            "Los parámetros 'page' y 'size' deben ser números enteros."));
        }
    }

    // Helper para respuestas de error homogéneas
    private Map<String, Object> error(String code, String message) {
        return Map.of(
                "code", code,
                "message", message
        );
    }


}
