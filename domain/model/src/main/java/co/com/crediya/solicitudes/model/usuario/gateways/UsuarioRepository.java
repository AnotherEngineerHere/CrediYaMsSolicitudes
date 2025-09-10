package co.com.crediya.solicitudes.model.usuario.gateways;

import co.com.crediya.solicitudes.model.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {
    Mono<Usuario> findByDocumentoIdentidad(String documentoIdentidad);
}
