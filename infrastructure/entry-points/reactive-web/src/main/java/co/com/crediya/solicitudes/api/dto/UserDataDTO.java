package co.com.crediya.solicitudes.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para datos básicos de usuario obtenidos del microservicio de autenticaciones.
 */
@Schema(description = "Datos básicos de usuario para enriquecimiento de datos")
public record UserDataDTO(
        @Schema(description = "Correo electrónico del usuario", example = "usuario@email.com")
        String email,

        @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
        String nombre,

        @Schema(description = "Salario base del usuario", example = "3000000")
        Long salarioBase,

        @Schema(description = "Rol del usuario", example = "ASESOR")
        Long rol
) {}