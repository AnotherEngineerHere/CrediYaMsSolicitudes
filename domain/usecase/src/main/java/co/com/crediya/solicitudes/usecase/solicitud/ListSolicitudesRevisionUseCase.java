package co.com.crediya.solicitudes.usecase.solicitud;

import co.com.crediya.solicitudes.model.solicitud.*;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public class ListSolicitudesRevisionUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    public Mono<PagedResult<SolicitudRevision>> execute(FiltroSolicitud filtro, PageQuery page) {
        int safeSize = Math.min(Math.max(page.getSize(), 1), 200);
        PageQuery safe = new PageQuery(
                Math.max(page.getPage(), 0),
                safeSize,
                page.getSortBy() == null ? "id_solicitud" : page.getSortBy(),
                page.isAsc()
        );

        return solicitudRepository.listarPendientes(filtro, safe)
                .flatMap(pagedResult -> {
                    return Flux.fromIterable(pagedResult.getItems())
                            .flatMap(this::enrichSolicitud)
                            .collectList()
                            .map(list -> new PagedResult<>(
                                    list,
                                    pagedResult.getTotal(),
                                    pagedResult.getPage(),
                                    pagedResult.getSize()
                            ));
                });
    }

    private Mono<SolicitudRevision> enrichSolicitud(Solicitud solicitud) {
        return usuarioRepository.findByDocumentoIdentidad(solicitud.getDocumentoIdentidad())
                .map(usuario -> SolicitudRevision.builder()
                        .monto(solicitud.getMonto())
                        .plazo(solicitud.getPlazo())
                        .email(solicitud.getEmail())
                        .nombre(usuario.getNombre() + " " + usuario.getApellido())
                        .tipoPrestamo(solicitud.getTipoPrestamo().getIdTipoPrestamo()+"")
                        .tasaInteres(solicitud.getTipoPrestamo().getTasaInteres())
                        .estadoSolicitud(solicitud.getEstado().getDescripcion())
                        .salarioBase(usuario.getSalario_base())
                        .montoMensualSolicitud(calcularMontoMensual(solicitud))
                        .build()
                );
    }

    private BigDecimal calcularMontoMensual(Solicitud solicitud) {
        BigDecimal monto = solicitud.getMonto();
        BigDecimal tasaInteres = solicitud.getTipoPrestamo().getTasaInteres();
        Integer plazo = solicitud.getPlazo();

        if (monto == null || tasaInteres == null || plazo == null || plazo == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal tasaMensual = tasaInteres.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP)
                .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);

        if (tasaMensual.compareTo(BigDecimal.ZERO) == 0) {
            return monto.divide(new BigDecimal(plazo), 2, RoundingMode.HALF_UP);
        }

        BigDecimal factor = (BigDecimal.ONE.add(tasaMensual)).pow(plazo);
        BigDecimal numerador = monto.multiply(tasaMensual).multiply(factor);
        BigDecimal denominador = factor.subtract(BigDecimal.ONE);

        if (denominador.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
    }
}
