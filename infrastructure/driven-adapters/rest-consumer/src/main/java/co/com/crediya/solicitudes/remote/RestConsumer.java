package co.com.crediya.solicitudes.remote;

import co.com.crediya.solicitudes.model.usuario.Usuario;
import co.com.crediya.solicitudes.model.usuario.gateways.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RestConsumer implements UsuarioRepository {

    private final WebClient webClient;

    public RestConsumer(WebClient.Builder webClientBuilder, @Value("${services.auth.url}") String authUrl) {
        this.webClient = webClientBuilder.baseUrl(authUrl).build();
    }

    @Override
    public Mono<Usuario> findByDocumentoIdentidad(String documentoIdentidad) {
        return webClient.get()
                .uri("/api/v1/usuarios/documento/{documentoIdentidad}", documentoIdentidad)
                .retrieve()
                .bodyToMono(Usuario.class);
    }
}
