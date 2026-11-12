package co.com.crediya.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "tipo_prestamo",
        uniqueConstraints = @UniqueConstraint(name = TipoPrestamoEntity.UK_NOMBRE, columnNames = "nombre")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoPrestamoEntity {

    public static final String UK_NOMBRE = "uk_tipo_prestamo_nombre";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_prestamo")
    private Long idTipoPrestamo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "monto_minimo", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoMinimo;

    @Column(name = "monto_maximo", nullable = false, precision = 15, scale = 2)
    private BigDecimal montoMaximo;

    @Column(name = "tasa_interes", nullable = false, precision = 5, scale = 2)
    private BigDecimal tasaInteres;

    @Column(name = "validacion_automatica", nullable = false)
    private Boolean validacionAutomatica;
}
