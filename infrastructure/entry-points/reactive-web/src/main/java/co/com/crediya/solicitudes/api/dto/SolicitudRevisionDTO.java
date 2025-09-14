package co.com.crediya.solicitudes.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para datos de solicitud en proceso de revisión por parte del asesor.
 *
 * Este DTO contiene toda la información necesaria para que un asesor pueda
 * tomar una decisión sobre una solicitud de crédito. Los datos incluyen
 * información financiera del solicitante obtenida desde el microservicio
 * de autenticación, así como cálculos automáticos como el monto mensual.
 *
 * Campos principales:
 * - Información básica de la solicitud (monto, plazo, tipo)
 * - Datos del solicitante (email, nombre, salario base)
 * - Información calculada (monto mensual, tasa de interés)
 * - Estado actual de la solicitud
 *
 * @author CrediYa Development Team
 * @version 1.0
 * @since 2024
 */
@Getter
@Setter
@Builder
@Schema(description = "Datos de solicitud en proceso de revisión")
public class SolicitudRevisionDTO {

    @Schema(description = "Monto solicitado para el crédito", example = "5000000.00")
    private BigDecimal monto;

    @Schema(description = "Plazo del crédito en meses", example = "24")
    private Integer plazo;

    @Schema(description = "Correo electrónico del solicitante", example = "usuario@email.com")
    private String email;

    @Schema(description = "Nombre completo del solicitante", example = "Juan Pérez")
    private String nombre;

    @Schema(description = "Tipo de préstamo solicitado", example = "Crédito de consumo")
    private String tipoPrestamo;

    @Schema(description = "Tasa de interés aplicada", example = "0.025")
    private BigDecimal tasaInteres;

    @Schema(description = "Estado actual de la solicitud", example = "EN_REVISION", allowableValues = {"CREADA", "EN_REVISION", "APROBADA", "RECHAZADA"})
    private String estadoSolicitud;

    @Schema(description = "Salario base del solicitante", example = "3000000")
    private Long salarioBase;

    @Schema(description = "Monto mensual calculado para la solicitud", example = "250000.00")
    private BigDecimal montoMensualSolicitud;
}
