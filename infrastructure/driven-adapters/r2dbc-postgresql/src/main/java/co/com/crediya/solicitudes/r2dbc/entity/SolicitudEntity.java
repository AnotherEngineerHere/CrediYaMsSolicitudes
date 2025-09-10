package co.com.crediya.solicitudes.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("core.solicitud")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@ToString
public class SolicitudEntity {

    @Id
    @Column("id_solicitud")
    private Long id;

    @Column("monto")
    private BigDecimal monto;

    @Column("plazo")
    private Integer plazo; // meses

    // FK a auth.usuario(email)
    @Column("email")
    private String email;

    // FK a core.estado(id_estado)
    @Column("id_estado")
    private Long idEstado;

    // FK a core.tipo_prestamo(id_tipo_prestamo)
    @Column("id_tipo_prestamo")
    private Long idTipoPrestamo;

    @Column("documento_identidad")
    private String documentoIdentidad;
}
