package co.com.crediya.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("tipo_prestamo")
public class TipoPrestamoEntity {
    @Id
    @Column("id_tipo_prestamo")
    private Long idTipoPrestamo;
    @Column("nombre")
    private String nombre;
    @Column("monto_minimo")
    private BigDecimal montoMinimo;
    @Column("monto_maximo")
    private BigDecimal montoMaximo;
    @Column("tasa_interes")
    private BigDecimal tasaInteres;
    @Column("validacion_automatica")
    private Boolean validacionAutomatica;
}
