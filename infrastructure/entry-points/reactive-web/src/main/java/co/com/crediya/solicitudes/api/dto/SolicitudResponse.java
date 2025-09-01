package co.com.crediya.solicitudes.api.dto;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SolicitudResponse {
    private Integer idSolicitud;
    private String documentoIdentidad;
    private String email;
    private BigDecimal monto;
    private Integer plazo;
    private Integer idTipoPrestamo;
    private String estado;           // CREADA / EN_REVISION / APROBADA / RECHAZADA
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
