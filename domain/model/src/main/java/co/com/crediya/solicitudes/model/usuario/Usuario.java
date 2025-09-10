package co.com.crediya.solicitudes.model.usuario;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class Usuario {
    private final String nombre;
    private final String apellido;
    private final String email;
    private final String documento_identidad;
    private final String telefono;
    private final Long rolId;
    private final Long salario_base;
    private final LocalDate fecha_nacimiento;
    private final String direccion;
}
