package co.com.crediya.solicitudes.api.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class SolicitudItemDTO {
    Integer idSolicitud;
    String documentoIdentidad;  // Documento del solicitante
    String email;
    BigDecimal monto;
    Integer plazo;
    String tipoPrestamo;        // Nombre del tipo de préstamo
    String estado;              // Nombre del estado de la solicitud
}
