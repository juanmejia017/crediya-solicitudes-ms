package co.com.crediya.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("estados")
public class EstadoEntity {
    @Id
    @Column("id_estado")
    private Long idEstado;
    @Column("nombre")
    private String nombre;
    @Column("descripcion")
    private String descripcion;
}
