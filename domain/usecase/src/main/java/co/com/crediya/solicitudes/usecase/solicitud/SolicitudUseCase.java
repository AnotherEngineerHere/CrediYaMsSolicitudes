package co.com.crediya.solicitudes.usecase.solicitud;

import co.com.crediya.solicitudes.model.estados.Estado;
import co.com.crediya.solicitudes.model.excepciones.DocumentoIdentidadInvalidoException;
import co.com.crediya.solicitudes.model.excepciones.DomainException;
import co.com.crediya.solicitudes.model.excepciones.EmailInvalidoException;
import co.com.crediya.solicitudes.model.excepciones.PlazoInvalidoException;
import co.com.crediya.solicitudes.model.solicitud.Solicitud;
import co.com.crediya.solicitudes.model.solicitud.gateways.SolicitudRepository;
import co.com.crediya.solicitudes.model.tipoprestamo.TipoPrestamo;
import co.com.crediya.solicitudes.usecase.estado.EstadoUseCase;
import co.com.crediya.solicitudes.usecase.tipoprestamo.TipoPrestamoUseCase;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final TipoPrestamoUseCase tipoPrestamoUseCase;
    private final EstadoUseCase estadoUseCase;

    public SolicitudUseCase(SolicitudRepository solicitudRepository,
                            TipoPrestamoUseCase tipoPrestamoUseCase,
                            EstadoUseCase estadoUseCase) {
        this.solicitudRepository = solicitudRepository;
        this.tipoPrestamoUseCase = tipoPrestamoUseCase;
        this.estadoUseCase = estadoUseCase;
    }

    // Regex simple para email
    private static final Pattern EMAIL_RX =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    /** Registra una nueva solicitud con estado CREADA. */
    public Mono<Solicitud> registrar(ComandoRegistrarSolicitud cmd) {
        validar(cmd);

        System.out.println("[SolicitudUseCase] Iniciando registro documento=" +
                cmd.getDocumentoIdentidad() + ", email=" + cmd.getEmail() +
                ", tipoPrestamoId=" + cmd.getIdTipoPrestamo());

        // 1) Verificar que el TipoPrestamo exista
        return tipoPrestamoUseCase.validarQueExista(cmd.getIdTipoPrestamo().longValue())
                // 2) Verificar que el estado CREADA exista en BD
                .then(estadoUseCase.validarExisteEstadoCreada())
                // 3) Construir la entidad y persistir
                .then(Mono.defer(() -> {
                    // Placeholder con el id de tipo de préstamo
                    TipoPrestamo tipoPrestamoLigero = TipoPrestamo.builder()
                            .idTipoPrestamo(cmd.getIdTipoPrestamo())
                            .build();

                    Solicitud nueva = Solicitud.builder()
                            .documentoIdentidad(cmd.getDocumentoIdentidad())
                            .email(cmd.getEmail())
                            .monto(cmd.getMonto())
                            .plazo(cmd.getPlazo())
                            .tipoPrestamo(tipoPrestamoLigero)
                            .estado(Estado.creada())
                            .build();

                    return solicitudRepository.save(nueva);
                }))
                .doOnSuccess(s ->
                        System.out.println("[SolicitudUseCase] Solicitud registrada id=" + s.getIdSolicitud() +
                                ", email=" + s.getEmail())
                )
                .doOnError(e ->
                        System.err.println("[SolicitudUseCase] Error registrando solicitud (email=" +
                                cmd.getEmail() + "): " + e.getMessage())
                );
    }

    // -------------------- Validaciones de entrada --------------------
    private void validar(ComandoRegistrarSolicitud cmd) {
        if (cmd == null) throw new DomainException("El comando de registrar solicitud es nulo") {};
        if (isBlank(cmd.getDocumentoIdentidad()))
            throw new DocumentoIdentidadInvalidoException(cmd.getDocumentoIdentidad());
        if (isBlank(cmd.getEmail()) || !EMAIL_RX.matcher(cmd.getEmail()).matches())
            throw new EmailInvalidoException(cmd.getEmail());
        if (cmd.getMonto() == null || menorOIgualACero(cmd.getMonto()))
            throw new DomainException("El monto es obligatorio y debe ser mayor a 0") {};
        if (cmd.getPlazo() == null || cmd.getPlazo() <= 0)
            throw new PlazoInvalidoException(""+cmd.getPlazo()); // <-- Integer, no String
        if (cmd.getIdTipoPrestamo() == null)
            throw new DomainException("El idTipoPrestamo es obligatorio") {};
    }

    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
    private boolean menorOIgualACero(BigDecimal n) { return n.signum() <= 0; }
}
