package co.com.crediya.solicitudes.api.dto;

import co.com.crediya.solicitudes.usecase.solicitud.ComandoRegistrarSolicitud;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegistrarSolicitudRequest {
    @NotBlank private String documentoIdentidad;
    @NotBlank @Email private String email;
    @NotNull @DecimalMin(value = "0.0", inclusive = false) private BigDecimal monto;
    @NotNull @Min(1) private Integer plazo;                 // meses
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
