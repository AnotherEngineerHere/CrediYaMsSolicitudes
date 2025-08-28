package co.com.crediya.solicitudes.model.solicitud;

import co.com.crediya.solicitudes.model.estados.Estado;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Solicitud {
    private Integer idSolicitud;
    private BigDecimal monto;
    private Integer plazo;               
    private String email;                
    private Estado estado;               
    private TipoPrestamo tipoPrestamo;   
    private String documentoIdentidad;   
}
