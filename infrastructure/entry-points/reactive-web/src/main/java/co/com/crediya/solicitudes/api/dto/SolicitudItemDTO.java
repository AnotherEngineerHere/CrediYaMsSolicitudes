package co.com.crediya.solicitudes.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * DTO para representar un elemento de solicitud en listados paginados.
 */
@Value
@Builder
@Schema(description = "Elemento de solicitud en listados paginados")
public class SolicitudItemDTO {

    @Schema(description = "ID único de la solicitud", example = "123")
    Integer idSolicitud;

    @Schema(description = "Número de documento de identidad del solicitante", example = "12345678")
    String documentoIdentidad;  // Documento del solicitante

    @Schema(description = "Correo electrónico del solicitante", example = "usuario@email.com")
    String email;

    @Schema(description = "Monto solicitado para el crédito", example = "5000000.00")
    BigDecimal monto;

    @Schema(description = "Plazo del crédito en meses", example = "24")
    Integer plazo;

    @Schema(description = "Nombre del tipo de préstamo solicitado", example = "Crédito de consumo")
    String tipoPrestamo;        // Nombre del tipo de préstamo

    @Schema(description = "Nombre del estado actual de la solicitud", example = "EN_REVISION", allowableValues = {"CREADA", "EN_REVISION", "APROBADA", "RECHAZADA"})
    String estado;              // Nombre del estado de la solicitud

    /**
     * Convierte una entidad de dominio Solicitud a SolicitudItemDTO.
     *
     * @param solicitud la entidad de dominio
     * @return DTO convertido
     */
    public static SolicitudItemDTO fromDomain(co.com.crediya.solicitudes.model.solicitud.Solicitud solicitud) {
        return SolicitudItemDTO.builder()
                .idSolicitud(solicitud.getIdSolicitud())
                .documentoIdentidad(solicitud.getDocumentoIdentidad())
                .email(solicitud.getEmail())
                .monto(solicitud.getMonto())
                .plazo(solicitud.getPlazo())
                .tipoPrestamo(solicitud.getTipoPrestamo() != null ? solicitud.getTipoPrestamo().getNombre() : null)
                .estado(solicitud.getEstado() != null ? solicitud.getEstado().getTipo().name() : null)
                .build();
    }
}
