package co.com.crediya.solicitudes.api.service;

import co.com.crediya.solicitudes.api.dto.UserDataDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Servicio para comunicación con el microservicio de autenticaciones.
 * Maneja las llamadas HTTP para obtener datos de usuarios.
 *
 * @author CrediYa Development Team
 * @version 1.0
 * @since 2024
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final WebClient webClient;

    @Value("${autenticaciones.service.url:http://localhost:8081}")
    private String autenticacionesServiceUrl;

    /**
     * Obtiene los datos básicos de un usuario por su correo electrónico.
     *
     * @param email correo electrónico del usuario
     * @return Mono con los datos del usuario o vacío si no existe
     */
    public Mono<UserDataDTO> getUserData(String email) {
        log.info("Consultando datos de usuario para email: {}", email);

        return webClient.get()
                .uri(autenticacionesServiceUrl + "/api/v1/usuarios/{email}", email)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    log.warn("Usuario no encontrado para email: {}", email);
                    return Mono.empty();
                })
                .onStatus(status -> status.isError(), response -> {
                    log.error("Error al consultar usuario {}: HTTP {}", email, response.statusCode());
                    return Mono.error(new RuntimeException("Error al consultar datos del usuario"));
                })
                .bodyToMono(UserDataDTO.class)
                .doOnNext(userData -> log.info("Datos obtenidos para usuario: {}", userData.email()))
                .doOnError(error -> log.error("Error al obtener datos de usuario {}: {}", email, error.getMessage()))
                .onErrorResume(throwable -> Mono.empty());
    }
}