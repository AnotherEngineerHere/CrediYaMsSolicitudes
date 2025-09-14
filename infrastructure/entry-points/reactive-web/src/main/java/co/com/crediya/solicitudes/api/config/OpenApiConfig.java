package co.com.crediya.solicitudes.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI/Swagger documentation.
 * Provides API metadata and configuration for the CrediYa Solicitudes service.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI specification for the CrediYa Solicitudes API.
     *
     * @return OpenAPI configuration with API metadata
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("CrediYa - Solicitudes")
                        .version("v1")
                        .description("API para gestión de solicitudes de crédito (registro, consulta, revisión, etc.)")
                        .contact(new Contact()
                                .name("Equipo CrediYa")
                                .email("dev@crediya.com"))
                        .license(new License()
                                .name("Apache-2.0")));
    }
}