package co.com.crediya.solicitudes.r2dbc.mapper;

import co.com.crediya.solicitudes.model.enums.EstadoTipo;
import co.com.crediya.solicitudes.model.estados.Estado;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.r2dbc.entity.SolicitudEntity;
import org.springframework.stereotype.Component;

@Component
public class SolicitudMapper {

    public Solicitud toDomain(SolicitudEntity e) {
        Estado estado = null;
        if (e.getIdEstado() != null) {
            EstadoTipo tipo = null;
            for (EstadoTipo t : EstadoTipo.values()) {
                if (t.getId() == Math.toIntExact(e.getIdEstado())) {
                    tipo = t;
                    break;
                }
            }
            estado = Estado.builder()
                    .idEstado(e.getIdEstado().intValue())
                    .tipo(tipo)
                    .descripcion(tipo != null ? tipo.getDescripcion() : null)
                    .build();
        }

        TipoPrestamo tp = null;
        if (e.getIdTipoPrestamo() != null) {
            tp = TipoPrestamo.builder()
                    .idTipoPrestamo(Math.toIntExact(e.getIdTipoPrestamo()))
                    .build();
        }

        return Solicitud.builder()
                .idSolicitud(e.getId() == null ? null : Math.toIntExact(e.getId()))
                .monto(e.getMonto())
                .plazo(e.getPlazo())
                .email(e.getEmail())
                .documentoIdentidad(e.getDocumentoIdentidad())
                .estado(estado)
                .tipoPrestamo(tp)
                .build();
    }
}
