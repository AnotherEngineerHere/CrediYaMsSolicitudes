package co.com.crediya.solicitudes.model.tipoprestamo;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoPrestamo {
    private Integer idTipoPrestamo;
    private String nombre;
    private BigDecimal montoMinimo;
    private BigDecimal montoMaximo;
    private BigDecimal tasaInteres;
    private boolean validacionAutomatica;

    public boolean esMontoValido(BigDecimal monto) {
        return monto != null
                && montoMinimo != null
                && montoMaximo != null
                && monto.compareTo(montoMinimo) >= 0
                && monto.compareTo(montoMaximo) <= 0;
    }
}
