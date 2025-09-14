package co.com.crediya.solicitudes.api.dto;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para la respuesta de una solicitud de crédito registrada.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(description = "Respuesta con los datos de una solicitud de crédito registrada")
public class SolicitudResponse {

    @Schema(description = "ID único de la solicitud", example = "123")
    private Integer idSolicitud;

    @Schema(description = "Número de documento de identidad del solicitante", example = "12345678")
    private String documentoIdentidad;

    @Schema(description = "Correo electrónico del solicitante", example = "usuario@email.com")
    private String email;

    @Schema(description = "Monto solicitado para el crédito", example = "5000000.00")
    private BigDecimal monto;

    @Schema(description = "Plazo del crédito en meses", example = "24")
    private Integer plazo;

    @Schema(description = "ID del tipo de préstamo solicitado", example = "1")
    private Integer idTipoPrestamo;

    @Schema(description = "Estado actual de la solicitud", example = "CREADA", allowableValues = {"CREADA", "EN_REVISION", "APROBADA", "RECHAZADA"})
    private String estado;           // CREADA / EN_REVISION / APROBADA / RECHAZADA

    @Schema(description = "Descripción detallada del estado de la solicitud", example = "Solicitud creada exitosamente")
    private String descripcionEstado;

    public static SolicitudResponse fromDomain(Solicitud s) {
        return SolicitudResponse.builder()
                .idSolicitud(s.getIdSolicitud())
                .documentoIdentidad(s.getDocumentoIdentidad())
                .email(s.getEmail())
                .monto(s.getMonto())
                .plazo(s.getPlazo())
                .idTipoPrestamo(s.getTipoPrestamo() != null ? s.getTipoPrestamo().getIdTipoPrestamo() : null)
                .estado(s.getEstado() != null && s.getEstado().getTipo() != null ? s.getEstado().getTipo().name() : null)
                .descripcionEstado(s.getEstado() != null ? s.getEstado().getDescripcion() : null)
                .build();
    }
}
