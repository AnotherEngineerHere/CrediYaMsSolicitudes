package co.com.crediya.solicitudes.api.dto;

import co.com.crediya.solicitudes.usecase.solicitud.ComandoRegistrarSolicitud;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para la solicitud de registro de una nueva solicitud de crédito.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "Datos requeridos para registrar una nueva solicitud de crédito")
public class RegistrarSolicitudRequest {

    @Schema(description = "Número de documento de identidad del solicitante", example = "12345678", required = true, minLength = 5, maxLength = 20)
    @NotBlank private String documentoIdentidad;

    @Schema(description = "Correo electrónico del solicitante", example = "usuario@email.com", required = true, format = "email")
    @NotBlank @Email private String email;

    @Schema(description = "Monto solicitado para el crédito", example = "5000000.00", required = true, minimum = "0.01")
    @NotNull @DecimalMin(value = "0.0", inclusive = false) private BigDecimal monto;

    @Schema(description = "Plazo del crédito en meses", example = "24", required = true, minimum = "1", maximum = "360")
    @NotNull @Min(1) private Integer plazo;                 // meses

    @Schema(description = "ID del tipo de préstamo solicitado", example = "1", required = true)
    @NotNull private Integer idTipoPrestamo;

    public ComandoRegistrarSolicitud toCommand() {
        return ComandoRegistrarSolicitud.builder()
                .documentoIdentidad(documentoIdentidad)
                .email(email)
                .monto(monto)
                .plazo(plazo)
                .idTipoPrestamo(idTipoPrestamo)
                .build();
    }
}
