package co.com.crediya.solicitudes.model.estados;

import co.com.crediya.solicitudes.model.enums.EstadoTipo;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estado {
    private Integer idEstado;
    private EstadoTipo tipo;
    private String descripcion;

    public static Estado creada() {
        return Estado.builder()
                .idEstado(EstadoTipo.EN_REVISION.getId())
                .tipo(EstadoTipo.EN_REVISION)
                .descripcion(EstadoTipo.EN_REVISION.getDescripcion())
                .build();
    }
}
