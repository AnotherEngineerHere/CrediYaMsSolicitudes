package co.com.crediya.solicitudes.model.solicitud;

import lombok.Builder;
import lombok.Value;
import java.math.BigDecimal;

@Value
@Builder
public class FiltroSolicitud {
    String email;
    String nombre;
    String tipoPrestamo;
    BigDecimal minMonto;
    BigDecimal maxMonto;
}