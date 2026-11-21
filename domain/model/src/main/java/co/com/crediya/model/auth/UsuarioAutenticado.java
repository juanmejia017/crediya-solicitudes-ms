package co.com.crediya.model.auth;

import co.com.crediya.model.rol.Rol;

import java.util.Arrays;

/**
 * Quién hizo la petición, según su token: lo que el sistema necesita para decidir permisos.
 */
public record UsuarioAutenticado(Long idUsuario, String correoElectronico, String documentoIdentidad, Rol rol) {

    public boolean tieneAlgunRol(Rol... roles) {
        return Arrays.asList(roles).contains(rol);
    }
}
