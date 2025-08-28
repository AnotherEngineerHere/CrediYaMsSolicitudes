package co.com.crediya.solicitudes.model.estados;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// =============================
//  Clase: Estado
// =============================
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estados {
    private Integer idEstado;
    private String nombre;
    private String descripcion;

    public static Estados pendienteRevision() {
        return Estados.builder()
                .nombre("Pendiente de revisión")
                .descripcion("Solicitud registrada y pendiente de validación manual o automática")
                .build();
    }
}
