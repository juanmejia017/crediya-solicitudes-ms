CREATE TABLE IF NOT EXISTS estados (
    id_estado   BIGSERIAL PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS tipo_prestamo (
    id_tipo_prestamo      BIGSERIAL PRIMARY KEY,
    nombre                VARCHAR(100)   NOT NULL UNIQUE,
    monto_minimo          NUMERIC(15, 2) NOT NULL,
    monto_maximo          NUMERIC(15, 2) NOT NULL,
    tasa_interes          NUMERIC(5, 2)  NOT NULL,
    validacion_automatica BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT ck_tipo_prestamo_rango CHECK (monto_minimo > 0 AND monto_maximo >= monto_minimo)
);

CREATE TABLE IF NOT EXISTS solicitud (
    id_solicitud        BIGSERIAL PRIMARY KEY,
    monto               NUMERIC(15, 2) NOT NULL CHECK (monto > 0),
    plazo               INTEGER        NOT NULL CHECK (plazo > 0),
    email               VARCHAR(150)   NOT NULL,
    documento_identidad VARCHAR(20)    NOT NULL,
    id_estado           BIGINT         NOT NULL REFERENCES estados (id_estado),
    id_tipo_prestamo    BIGINT         NOT NULL REFERENCES tipo_prestamo (id_tipo_prestamo)
);
