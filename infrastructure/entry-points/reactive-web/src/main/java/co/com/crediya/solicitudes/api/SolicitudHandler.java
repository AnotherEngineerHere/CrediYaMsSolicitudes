package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.RegistrarSolicitudRequest;
import co.com.crediya.solicitudes.api.dto.SolicitudResponse;
import co.com.crediya.solicitudes.model.excepciones.*;
import co.com.crediya.solicitudes.usecase.solicitud.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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

    /**
     * Maneja la creación de una nueva solicitud de crédito.
     *
     * @param request cuerpo de la petición con datos de la solicitud.
     * @return {@link ServerResponse} con estado 201 Created y datos de la solicitud creada.
     */
    public Mono<ServerResponse> registrar(ServerRequest request) {
        System.out.println("Solicitud para crear una nueva solicitud recibida");

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
}
