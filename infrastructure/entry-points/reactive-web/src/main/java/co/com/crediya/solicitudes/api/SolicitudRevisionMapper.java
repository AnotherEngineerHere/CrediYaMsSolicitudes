package co.com.crediya.solicitudes.api;

import co.com.crediya.solicitudes.api.dto.SolicitudRevisionDTO;
import co.com.crediya.solicitudes.model.solicitud.SolicitudRevision;

public class SolicitudRevisionMapper {

    public static SolicitudRevisionDTO toDto(SolicitudRevision solicitud) {
        return SolicitudRevisionDTO.builder()
                .monto(solicitud.getMonto())
                .plazo(solicitud.getPlazo())
                .email(solicitud.getEmail())
                .nombre(solicitud.getNombre())
                .tipoPrestamo(solicitud.getTipoPrestamo())
                .tasaInteres(solicitud.getTasaInteres())
                .estadoSolicitud(solicitud.getEstadoSolicitud())
                .salarioBase(solicitud.getSalarioBase())
                .montoMensualSolicitud(solicitud.getMontoMensualSolicitud())
                .build();
    }
}
