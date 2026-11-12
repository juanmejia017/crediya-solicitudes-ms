package co.com.crediya.model.estado;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Estado {
    public static final String PENDIENTE_REVISION = "Pendiente de revisión";

    private Long idEstado;
    private String nombre;
    private String descripcion;
}
