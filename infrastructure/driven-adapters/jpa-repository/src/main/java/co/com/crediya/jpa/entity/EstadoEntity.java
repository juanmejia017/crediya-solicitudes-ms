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

@Entity
@Table(
        name = "estados",
        uniqueConstraints = @UniqueConstraint(name = EstadoEntity.UK_NOMBRE, columnNames = "nombre")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoEntity {

    public static final String UK_NOMBRE = "uk_estados_nombre";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado")
    private Long idEstado;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;
}
