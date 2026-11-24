package cl.duoc.veterinaria.usuarios.dto;

import jakarta.validation.constraints.NotNull;

/** Activar o desactivar una cuenta */
public record EstadoRequest(@NotNull(message = "Indique si el usuario queda activo") Boolean activo) {
}
