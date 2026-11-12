INSERT INTO estados (nombre, descripcion) VALUES
    ('Pendiente de revisión', 'La solicitud fue registrada y espera ser evaluada'),
    ('Revisión manual', 'La solicitud requiere revisión por un asesor'),
    ('Aprobada', 'La solicitud fue aprobada'),
    ('Rechazada', 'La solicitud fue rechazada')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO tipo_prestamo (nombre, monto_minimo, monto_maximo, tasa_interes, validacion_automatica) VALUES
    ('Libre inversión', 1000000, 50000000, 1.80, TRUE),
    ('Vehículo', 5000000, 150000000, 1.20, FALSE),
    ('Vivienda', 20000000, 500000000, 0.90, FALSE),
    ('Educativo', 500000, 30000000, 1.00, TRUE)
ON CONFLICT (nombre) DO NOTHING;
