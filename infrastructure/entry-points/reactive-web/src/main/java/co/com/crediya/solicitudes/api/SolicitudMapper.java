package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.SolicitudItemDTO;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;

public final class SolicitudMapper {

    private SolicitudMapper() {}

    public static SolicitudItemDTO toDto(Solicitud s) {
        if (s == null) return null;

        return SolicitudItemDTO.builder()
                .idSolicitud(s.getIdSolicitud())
                .documentoIdentidad(s.getDocumentoIdentidad())
                .email(s.getEmail())
                .monto(s.getMonto())
                .plazo(s.getPlazo())
                .tipoPrestamo(s.getTipoPrestamo() != null ? s.getTipoPrestamo().getNombre() : null)
                .estado(s.getEstado() != null ? s.getEstado().getDescripcion() : null)
                .build();
    }
}
