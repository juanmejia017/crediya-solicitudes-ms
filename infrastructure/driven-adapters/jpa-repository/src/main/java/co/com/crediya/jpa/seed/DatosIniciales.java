package co.com.crediya.jpa.seed;

import co.com.crediya.jpa.entity.EstadoEntity;
import co.com.crediya.jpa.entity.TipoPrestamoEntity;
import co.com.crediya.jpa.repository.EstadoJpaRepository;
import co.com.crediya.jpa.repository.TipoPrestamoJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Carga los estados y tipos de préstamo iniciales al arrancar, solo si todavía no existen.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatosIniciales implements ApplicationRunner {

    static final List<EstadoEntity> ESTADOS = List.of(
            estado("Pendiente de revisión", "La solicitud fue registrada y espera ser evaluada"),
            estado("Revisión manual", "La solicitud requiere revisión por un asesor"),
            estado("Aprobada", "La solicitud fue aprobada"),
            estado("Rechazada", "La solicitud fue rechazada"));

    static final List<TipoPrestamoEntity> TIPOS = List.of(
            tipo("Libre inversión", "1000000", "50000000", "1.80", true),
            tipo("Vehículo", "5000000", "150000000", "1.20", false),
            tipo("Vivienda", "20000000", "500000000", "0.90", false),
            tipo("Educativo", "500000", "30000000", "1.00", true));

    private final EstadoJpaRepository estadoRepository;
    private final TipoPrestamoJpaRepository tipoPrestamoRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        ESTADOS.stream()
                .filter(estado -> !estadoRepository.existsByNombre(estado.getNombre()))
                .forEach(estado -> {
                    estadoRepository.save(EstadoEntity.builder()
                            .nombre(estado.getNombre()).descripcion(estado.getDescripcion()).build());
                    log.info("Estado inicial creado: {}", estado.getNombre());
                });

        TIPOS.stream()
                .filter(tipo -> !tipoPrestamoRepository.existsByNombre(tipo.getNombre()))
                .forEach(tipo -> {
                    tipoPrestamoRepository.save(TipoPrestamoEntity.builder()
                            .nombre(tipo.getNombre()).montoMinimo(tipo.getMontoMinimo())
                            .montoMaximo(tipo.getMontoMaximo()).tasaInteres(tipo.getTasaInteres())
                            .validacionAutomatica(tipo.getValidacionAutomatica()).build());
                    log.info("Tipo de préstamo inicial creado: {}", tipo.getNombre());
                });
    }

    private static EstadoEntity estado(String nombre, String descripcion) {
        return EstadoEntity.builder().nombre(nombre).descripcion(descripcion).build();
    }

    private static TipoPrestamoEntity tipo(String nombre, String minimo, String maximo, String tasa, boolean automatica) {
        return TipoPrestamoEntity.builder()
                .nombre(nombre)
                .montoMinimo(new BigDecimal(minimo))
                .montoMaximo(new BigDecimal(maximo))
                .tasaInteres(new BigDecimal(tasa))
                .validacionAutomatica(automatica)
                .build();
    }
}
