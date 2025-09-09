package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SolicitudMapperTest {

    @Test
    void toDto_withCompleteSolicitud_shouldMapToDto() {
        var solicitud = Solicitud.builder()
                .idSolicitud(1)
                .documentoIdentidad("12345")
                .email("test@test.com")
                .monto(BigDecimal.TEN)
                .plazo(12)
                .tipoPrestamo(TipoPrestamo.builder().idTipoPrestamo(1).nombre("Test").build())
                .build();

        var dto = SolicitudMapper.toDto(solicitud);

        assertNotNull(dto);
        assertEquals(solicitud.getIdSolicitud(), dto.getIdSolicitud());
        assertEquals(solicitud.getDocumentoIdentidad(), dto.getDocumentoIdentidad());
        assertEquals(solicitud.getEmail(), dto.getEmail());
        assertEquals(solicitud.getMonto(), dto.getMonto());
        assertEquals(solicitud.getPlazo(), dto.getPlazo());
        assertEquals(solicitud.getTipoPrestamo().getNombre(), dto.getTipoPrestamo());
    }

    @Test
    void toDto_withNullSolicitud_shouldReturnNull() {
        var dto = SolicitudMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void toDto_withNullTipoPrestamo_shouldMapToDtoWithNullTipoPrestamo() {
        var solicitud = Solicitud.builder().tipoPrestamo(null).build();
        var dto = SolicitudMapper.toDto(solicitud);
        assertNotNull(dto);
        assertNull(dto.getTipoPrestamo());
    }
}
