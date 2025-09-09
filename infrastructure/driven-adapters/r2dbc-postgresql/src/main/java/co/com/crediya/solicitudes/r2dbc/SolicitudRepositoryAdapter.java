package co.com.crediya.solicitudes.r2dbc;

import co.com.crediya.solicitudes.model.estados.Estado;
import co.com.crediya.solicitudes.model.enums.EstadoTipo;
import co.com.crediya.solicitudes.model.solicitud.FiltroSolicitud;
import co.com.crediya.solicitudes.model.solicitud.PageQuery;
import co.com.crediya.solicitudes.model.solicitud.PagedResult;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.r2dbc.entity.SolicitudEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class SolicitudRepositoryAdapter implements SolicitudRepository {

    private static final Logger log = LoggerFactory.getLogger(SolicitudRepositoryAdapter.class);

    private final SolicitudReactiveRepository repository;

    public SolicitudRepositoryAdapter(SolicitudReactiveRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<PagedResult<Solicitud>> listarPendientes(FiltroSolicitud filtro, PageQuery page) {
        return repository.listarPendientes(filtro,page);
    }
    @Override
    public Mono<Solicitud> save(Solicitud solicitud) {
        if (solicitud == null) {
            return Mono.error(new IllegalArgumentException("La solicitud no puede ser nula"));
        }
        SolicitudEntity entity = toEntity(solicitud);

        return repository.save(entity)
                .map(saved -> {
                    // Devolvemos la solicitud original con el ID asignado (conserva campos no persistidos como documentoIdentidad)
                    return solicitud.toBuilder()
                            .idSolicitud(saved.getId() == null ? null : Math.toIntExact(saved.getId()))
                            .build();
                })
                .doOnSuccess(s -> log.info("Solicitud guardada en DB: id={}, email={}", s.getIdSolicitud(), s.getEmail()))
                .doOnError(e -> log.error("Error al guardar solicitud (email={}): {}", solicitud.getEmail(), e.getMessage()));
    }

    // -------------------- Mapper manual Entidad <-> Dominio --------------------
    private static SolicitudEntity toEntity(Solicitud d) {
        Long idEstado = null;
        if (d.getEstado() != null) {
            if (d.getEstado().getIdEstado() != null) {
                idEstado = d.getEstado().getIdEstado().longValue();
            } else if (d.getEstado().getTipo() != null) {
                idEstado = (long) d.getEstado().getTipo().getId();
            }
        }
        Long idTipoPrestamo = (d.getTipoPrestamo() != null && d.getTipoPrestamo().getIdTipoPrestamo() != null)
                ? d.getTipoPrestamo().getIdTipoPrestamo().longValue()
                : null;

        return SolicitudEntity.builder()
                .id(d.getIdSolicitud() == null ? null : d.getIdSolicitud().longValue())
                .monto(d.getMonto())
                .plazo(d.getPlazo())
                .email(d.getEmail())
                .idEstado(idEstado)
                .idTipoPrestamo(idTipoPrestamo)
                .build();
    }

    @SuppressWarnings("unused")
    private static Solicitud toDomain(SolicitudEntity e) {
        // Reconstrucción mínima (sin joins)
        Estado estado = null;
        if (e.getIdEstado() != null) {
            EstadoTipo tipo = null;
            for (EstadoTipo t : EstadoTipo.values()) {
                if (t.getId() == Math.toIntExact(e.getIdEstado())) { tipo = t; break; }
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
                .estado(estado)
                .tipoPrestamo(tp)
                .build();
    }
}
