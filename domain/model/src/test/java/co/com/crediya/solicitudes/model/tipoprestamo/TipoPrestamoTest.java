package co.com.crediya.solicitudes.model.tipoprestamo;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TipoPrestamoTest {

    @Test
    void esMontoValido_withMontoInRange_shouldReturnTrue() {
        var tipoPrestamo = TipoPrestamo.builder()
                .montoMinimo(new BigDecimal("100"))
                .montoMaximo(new BigDecimal("1000"))
                .build();
        assertTrue(tipoPrestamo.esMontoValido(new BigDecimal("500")));
    }

    @Test
    void esMontoValido_withMontoEqualToMin_shouldReturnTrue() {
        var tipoPrestamo = TipoPrestamo.builder()
                .montoMinimo(new BigDecimal("100"))
                .montoMaximo(new BigDecimal("1000"))
                .build();
        assertTrue(tipoPrestamo.esMontoValido(new BigDecimal("100")));
    }

    @Test
    void esMontoValido_withMontoEqualToMax_shouldReturnTrue() {
        var tipoPrestamo = TipoPrestamo.builder()
                .montoMinimo(new BigDecimal("100"))
                .montoMaximo(new BigDecimal("1000"))
                .build();
        assertTrue(tipoPrestamo.esMontoValido(new BigDecimal("1000")));
    }

    @Test
    void esMontoValido_withMontoBelowMin_shouldReturnFalse() {
        var tipoPrestamo = TipoPrestamo.builder()
                .montoMinimo(new BigDecimal("100"))
                .montoMaximo(new BigDecimal("1000"))
                .build();
        assertFalse(tipoPrestamo.esMontoValido(new BigDecimal("99")));
    }

    @Test
    void esMontoValido_withMontoAboveMax_shouldReturnFalse() {
        var tipoPrestamo = TipoPrestamo.builder()
                .montoMinimo(new BigDecimal("100"))
                .montoMaximo(new BigDecimal("1000"))
                .build();
        assertFalse(tipoPrestamo.esMontoValido(new BigDecimal("1001")));
    }

    @Test
    void esMontoValido_withNullMin_shouldReturnFalse() {
        var tipoPrestamo = TipoPrestamo.builder()
                .montoMinimo(null)
                .montoMaximo(new BigDecimal("1000"))
                .build();
        assertFalse(tipoPrestamo.esMontoValido(new BigDecimal("500")));
    }

    @Test
    void esMontoValido_withNullMax_shouldReturnFalse() {
        var tipoPrestamo = TipoPrestamo.builder()
                .montoMinimo(new BigDecimal("100"))
                .montoMaximo(null)
                .build();
        assertFalse(tipoPrestamo.esMontoValido(new BigDecimal("500")));
    }

    @Test
    void esMontoValido_withNullMinAndMax_shouldReturnFalse() {
        var tipoPrestamo = TipoPrestamo.builder()
                .montoMinimo(null)
                .montoMaximo(null)
                .build();
        assertFalse(tipoPrestamo.esMontoValido(new BigDecimal("500")));
    }
}
