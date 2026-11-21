package co.com.crediya.jpa.mapper;

import co.com.crediya.jpa.entity.EstadoEntity;
import co.com.crediya.jpa.entity.SolicitudEntity;
import co.com.crediya.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.model.estado.Estado;
import co.com.crediya.model.solicitud.Solicitud;
import co.com.crediya.model.tipoprestamo.TipoPrestamo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityMappersTest {

    @Test
    void laSolicitudIdaYVueltaConservaLosDatos() {
        Solicitud solicitud = Solicitud.builder()
                .idSolicitud(3L).monto(new BigDecimal("5000000")).plazo(24).email("ana@correo.com")
                .documentoIdentidad("1037654321").idEstado(1L).idTipoPrestamo(2L).build();

        SolicitudEntity entidad = SolicitudEntityMapper.aEntidad(solicitud);
        Solicitud deVuelta = SolicitudEntityMapper.aDominio(entidad);

        assertEquals(2L, entidad.getIdTipoPrestamo());
        assertEquals(solicitud.getMonto(), deVuelta.getMonto());
        assertEquals(solicitud.getEmail(), deVuelta.getEmail());
        assertEquals(solicitud.getIdEstado(), deVuelta.getIdEstado());
    }

    @Test
    void convierteElTipoDePrestamo() {
        TipoPrestamoEntity entidad = TipoPrestamoEntity.builder().idTipoPrestamo(2L).nombre("Vehículo")
                .montoMinimo(new BigDecimal("5000000")).montoMaximo(new BigDecimal("150000000"))
                .tasaInteres(new BigDecimal("1.20")).validacionAutomatica(false).build();

        TipoPrestamo tipo = TipoPrestamoEntityMapper.aDominio(entidad);

        assertEquals("Vehículo", tipo.getNombre());
        assertEquals(new BigDecimal("150000000"), tipo.getMontoMaximo());
    }

    @Test
    void convierteElEstado() {
        Estado estado = EstadoEntityMapper.aDominio(
                EstadoEntity.builder().idEstado(1L).nombre(Estado.PENDIENTE_REVISION).descripcion("Inicial").build());

        assertEquals(1L, estado.getIdEstado());
        assertEquals(Estado.PENDIENTE_REVISION, estado.getNombre());
    }
}
