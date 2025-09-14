package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.PagedResponseDTO;
import co.com.crediya.solicitudes.api.dto.RegistrarSolicitudRequest;
import co.com.crediya.solicitudes.api.dto.SolicitudItemDTO;
import co.com.crediya.solicitudes.api.dto.SolicitudResponse;
import co.com.crediya.solicitudes.api.dto.SolicitudRevisionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Configuración de rutas REST para el módulo de solicitudes de crédito.
 *
 * Esta clase define las rutas HTTP disponibles para la gestión completa del ciclo de vida
 * de solicitudes de crédito, desde el registro hasta la revisión por asesores.
 * Utiliza el enfoque funcional de Spring WebFlux con anotaciones de OpenAPI/Swagger
 * para documentación automática y completa de la API.
 *
 * Arquitectura: Implementa el patrón de arquitectura hexagonal (puertos y adaptadores)
 * separando claramente la lógica de dominio de la infraestructura.
 *
 * Seguridad: Todas las rutas están protegidas con autenticación JWT y control de acceso
 * basado en roles (CLIENTE, ASESOR, ADMIN).
 *
 * @author CrediYa Development Team
 * @version 1.1
 * @since 2024
 *
 * Rutas definidas:
 * - POST /api/v1/solicitud: Registrar nueva solicitud de crédito (CLIENTE/ADMIN)
 * - GET /api/v1/solicitud/pendientes: Listar solicitudes pendientes (CLIENTE)
 * - GET /api/v1/solicitud: Listar solicitudes para revisión (ASESOR)
 *
 * Características principales:
 * - Documentación OpenAPI/Swagger completa
 * - Paginación y filtrado avanzado
 * - Enriquecimiento de datos desde microservicio de autenticación
 * - Manejo robusto de errores con códigos específicos
 * - Logging detallado para monitoreo y debugging
 */
@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    consumes = MediaType.APPLICATION_JSON_VALUE,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "registrar",
                    operation = @Operation(
                            operationId = "registrarSolicitud",
                            summary = "Registrar nueva solicitud de crédito",
                            description = "Crea una nueva solicitud de crédito con la información proporcionada",
                            tags = {"Solicitudes"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = RegistrarSolicitudRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Solicitud creada exitosamente",
                                            content = @Content(schema = @Schema(implementation = SolicitudResponse.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos"),
                                    @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes"),
                                    @ApiResponse(responseCode = "422", description = "Error de validación de negocio"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud/pendientes",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "listar",
                    operation = @Operation(
                            operationId = "listarSolicitudesPendientes",
                            summary = "Listar solicitudes pendientes",
                            description = "Obtiene una lista paginada de solicitudes de crédito que están pendientes de revisión",
                            tags = {"Solicitudes"},
                            parameters = {
                                    @Parameter(
                                            name = "page",
                                            description = "Número de página (0-based)",
                                            in = ParameterIn.QUERY,
                                            schema = @Schema(type = "integer", defaultValue = "0")
                                    ),
                                    @Parameter(
                                            name = "size",
                                            description = "Tamaño de página",
                                            in = ParameterIn.QUERY,
                                            schema = @Schema(type = "integer", defaultValue = "10")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Lista de solicitudes obtenida exitosamente",
                                            content = @Content(schema = @Schema(implementation = PagedResponseDTO.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Parámetros de consulta inválidos"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    beanClass = SolicitudHandler.class,
                    beanMethod = "listarParaRevision",
                    operation = @Operation(
                            operationId = "listarSolicitudesParaRevision",
                            summary = "Listar solicitudes para revisión (ASESOR)",
                            description = "Obtiene una lista paginada de solicitudes que requieren revisión manual por parte del asesor. Solo accesible para usuarios con rol ASESOR.",
                            tags = {"Solicitudes", "Asesor"},
                            parameters = {
                                    @Parameter(
                                            name = "page",
                                            description = "Número de página (0-based)",
                                            in = ParameterIn.QUERY,
                                            schema = @Schema(type = "integer", defaultValue = "0")
                                    ),
                                    @Parameter(
                                            name = "size",
                                            description = "Tamaño de página",
                                            in = ParameterIn.QUERY,
                                            schema = @Schema(type = "integer", defaultValue = "10")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Lista de solicitudes para revisión obtenida exitosamente",
                                            content = @Content(schema = @Schema(implementation = PagedResponseDTO.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Parámetros de consulta inválidos"),
                                    @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ASESOR"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(SolicitudHandler handler) {
        return route(POST("/api/v1/solicitud"), handler::registrar)
                .andRoute(GET("/api/v1/solicitud/pendientes"), handler::listar)
                .andRoute(GET("/api/v1/solicitud"), handler::listarParaRevision);
    }
}
