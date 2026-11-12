package co.com.crediya.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "solicitud")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long idSolicitud;

    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(name = "plazo", nullable = false)
    private Integer plazo;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "documento_identidad", nullable = false, length = 20)
    private String documentoIdentidad;

    @Column(name = "id_estado", nullable = false)
    private Long idEstado;

    @Column(name = "id_tipo_prestamo", nullable = false)
    private Long idTipoPrestamo;
}
