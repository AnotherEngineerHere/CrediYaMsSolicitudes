package co.com.crediya.solicitudes.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EstadoTipo {
    CREADA(1, "Solicitud registrada"),
    EN_REVISION(2, "Solicitud en estudio"),
    APROBADA(3, "Solicitud aprobada"),
    RECHAZADA(4, "Solicitud rechazada");

    private final int id;
    private final String descripcion;
}
