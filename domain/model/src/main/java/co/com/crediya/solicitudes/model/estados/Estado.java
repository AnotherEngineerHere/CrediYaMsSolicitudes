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
                .idEstado(EstadoTipo.CREADA.getId())
                .tipo(EstadoTipo.CREADA)
                .descripcion(EstadoTipo.CREADA.getDescripcion())
                .build();
    }
}
