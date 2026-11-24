package cl.duoc.veterinaria.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

/** Token de renovación que entregó el login */
public record RenovarRequest(@NotBlank(message = "El token de renovación es obligatorio") String tokenRenovacion) {
}
