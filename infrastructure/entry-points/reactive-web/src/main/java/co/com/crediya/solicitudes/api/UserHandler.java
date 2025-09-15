package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.UserDataDTO;
import co.com.crediya.solicitudes.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Handler for user-related endpoints.
 *
 * This class handles operations related to user data retrieval.
 */
@Component
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Endpoints for user data management")
public class UserHandler {

    private final UserService userService;

    /**
     * Retrieves user data by email.
     *
     * This method processes the GET request to fetch basic user information by email.
     * Requires ASESOR role for access.
     *
     * @param request the HTTP request containing the email path variable
     * @return Mono<ServerResponse> with user data or 404 if not found
     */
    @Operation(
            summary = "Obtiene datos básicos de usuario por email",
            description = "Requiere rol ASESOR. Retorna 404 si el usuario no existe.",
            tags = {"Usuarios"}
    )
    @ApiResponse(responseCode = "200", description = "User data retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "403", description = "Access denied - ASESOR role required")
    public Mono<ServerResponse> getUserByEmail(ServerRequest request) {
        String email = request.pathVariable("email");
        return userService.getUserData(email)
                .flatMap(userData -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(userData))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}