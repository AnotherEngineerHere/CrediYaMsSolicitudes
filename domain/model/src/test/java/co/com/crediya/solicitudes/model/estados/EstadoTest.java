package co.com.crediya.solicitudes.model.estados;

import co.com.crediya.solicitudes.model.enums.EstadoTipo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EstadoTest {

    @Test
    void creada_shouldReturnEstadoEnRevision() {
        var estado = Estado.creada();

        assertNotNull(estado);
        assertEquals(EstadoTipo.EN_REVISION.getId(), estado.getIdEstado());
        assertEquals(EstadoTipo.EN_REVISION, estado.getTipo());
        assertEquals(EstadoTipo.EN_REVISION.getDescripcion(), estado.getDescripcion());
    }
}
