package co.com.crediya.solicitudes.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

/**
 * DTO para respuestas de error estandarizadas.
 */
@Value
@Builder
@Schema(description = "Respuesta de error estandarizada")
public class ErrorResponse {

    @Schema(description = "Código de error", example = "EMAIL_INVALIDO")
    String code;

    @Schema(description = "Mensaje descriptivo del error", example = "El formato del correo electrónico no es válido")
    String message;

    @Schema(description = "Timestamp del momento en que ocurrió el error", example = "2023-12-01T10:30:00Z")
    OffsetDateTime timestamp;

    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
