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
@Table("solicitud")
public class SolicitudEntity {
    @Id
    @Column("id_solicitud")
    private Long idSolicitud;
    @Column("monto")
    private BigDecimal monto;
    @Column("plazo")
    private Integer plazo;
    @Column("email")
    private String email;
    @Column("documento_identidad")
    private String documentoIdentidad;
    @Column("id_estado")
    private Long idEstado;
    @Column("id_tipo_prestamo")
    private Long idTipoPrestamo;
}
