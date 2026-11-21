package co.com.crediya.jpa.mapper;

import co.com.crediya.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;

/**
 * Convierte la entidad de la tabla tipo_prestamo en el modelo del dominio (TipoPrestamo).
 */
public final class TipoPrestamoEntityMapper {

    private TipoPrestamoEntityMapper() {
    }

    public static TipoPrestamo aDominio(TipoPrestamoEntity entity) {
        return TipoPrestamo.builder()
                .idTipoPrestamo(entity.getIdTipoPrestamo())
                .nombre(entity.getNombre())
                .montoMinimo(entity.getMontoMinimo())
                .montoMaximo(entity.getMontoMaximo())
                .tasaInteres(entity.getTasaInteres())
                .validacionAutomatica(entity.getValidacionAutomatica())
                .build();
    }
}
