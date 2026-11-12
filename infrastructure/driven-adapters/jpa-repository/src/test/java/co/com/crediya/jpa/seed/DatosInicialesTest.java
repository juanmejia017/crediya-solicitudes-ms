package co.com.crediya.jpa.seed;

import co.com.crediya.jpa.entity.EstadoEntity;
import co.com.crediya.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.jpa.repository.EstadoJpaRepository;
import co.com.crediya.jpa.repository.TipoPrestamoJpaRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DatosInicialesTest {

    private final EstadoJpaRepository estadoRepository = mock(EstadoJpaRepository.class);
    private final TipoPrestamoJpaRepository tipoPrestamoRepository = mock(TipoPrestamoJpaRepository.class);
    private final DatosIniciales datosIniciales = new DatosIniciales(estadoRepository, tipoPrestamoRepository);

    @Test
    void creaEstadosYTiposCuandoLaBaseEstaVacia() {
        when(estadoRepository.existsByNombre(anyString())).thenReturn(false);
        when(tipoPrestamoRepository.existsByNombre(anyString())).thenReturn(false);

        datosIniciales.run(null);

        verify(estadoRepository, times(DatosIniciales.ESTADOS.size())).save(any(EstadoEntity.class));
        verify(tipoPrestamoRepository, times(DatosIniciales.TIPOS.size())).save(any(TipoPrestamoEntity.class));
    }

    @Test
    void noDuplicaDatosQueYaExisten() {
        when(estadoRepository.existsByNombre(anyString())).thenReturn(true);
        when(tipoPrestamoRepository.existsByNombre(anyString())).thenReturn(true);

        datosIniciales.run(null);

        verify(estadoRepository, never()).save(any(EstadoEntity.class));
        verify(tipoPrestamoRepository, never()).save(any(TipoPrestamoEntity.class));
    }
}
