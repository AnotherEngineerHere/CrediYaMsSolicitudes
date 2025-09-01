package co.com.crediya.solicitudes.usecase.solicitud;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComandoRegistrarSolicitud {
    private String documentoIdentidad;
    private String email;
    private BigDecimal monto;
    private Integer plazo;            // en meses
    private Integer idTipoPrestamo;   // referencia al tipo de préstamo
}
