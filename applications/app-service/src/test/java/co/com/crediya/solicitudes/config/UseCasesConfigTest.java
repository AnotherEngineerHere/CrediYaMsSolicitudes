package co.com.crediya.solicitudes.config;

import co.com.crediya.solicitudes.model.estados.gateways.EstadosRepository;
import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitud;
import co.com.crediya.solicitudes.model.solicitud.PageQuery;
import co.com.crediya.solicitudes.model.solicitud.PagedResult;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.crediya.solicitudes.model.usuario.Usuario;
import co.com.crediya.solicitudes.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'UseCase' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public SolicitudRepository solicitudRepository() {
            return new SolicitudRepository() {
                @Override
                public Mono<Solicitud> save(Solicitud solicitud) {
                    return Mono.just(solicitud);
                }

                @Override
                public Mono<PagedResult<Solicitud>> listarPendientes(FiltroSolicitud filtro, PageQuery page) {
                    return Mono.empty();
                }
            };
        }

        @Bean
        public UsuarioRepository usuarioRepository() {
            return documentoIdentidad -> Mono.just(Usuario.builder().build());
        }

        @Bean
        public TipoPrestamoRepository tipoPrestamoRepository() {
            return idTipoPrestamo -> Mono.just(true);
        }

        @Bean
        public EstadosRepository estadosRepository() {
            return idEstado -> Mono.just(true);
        }
    }
}