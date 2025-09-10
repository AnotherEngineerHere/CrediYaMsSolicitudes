CREATE SCHEMA IF NOT EXISTS core;

CREATE TABLE IF NOT EXISTS core.estado (
    id_estado SERIAL PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS core.tipo_prestamo (
    id_tipo_prestamo SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    monto_minimo NUMERIC(15, 2) NOT NULL,
    monto_maximo NUMERIC(15, 2) NOT NULL,
    tasa_interes NUMERIC(5, 2) NOT NULL,
    validacion_automatica BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS core.solicitud (
    id_solicitud SERIAL PRIMARY KEY,
    monto NUMERIC(15, 2) NOT NULL,
    plazo INTEGER NOT NULL,
    email VARCHAR(255) NOT NULL,
    documento_identidad VARCHAR(255) NOT NULL,
    id_estado INTEGER NOT NULL,
    id_tipo_prestamo INTEGER NOT NULL,
    FOREIGN KEY (id_estado) REFERENCES core.estado(id_estado),
    FOREIGN KEY (id_tipo_prestamo) REFERENCES core.tipo_prestamo(id_tipo_prestamo)
);

-- Insert initial data
INSERT INTO core.estado (id_estado, descripcion) VALUES
(1, 'Solicitud registrada'),
(2, 'Solicitud en estudio'),
(3, 'Solicitud aprobada'),
(4, 'Solicitud rechazada'),
(5, 'Revision manual')
ON CONFLICT (id_estado) DO NOTHING;

INSERT INTO core.tipo_prestamo (id_tipo_prestamo, nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica) VALUES
(1, 'Libre inversión', 1000000, 50000000, 1.5, true)
ON CONFLICT (id_tipo_prestamo) DO NOTHING;
