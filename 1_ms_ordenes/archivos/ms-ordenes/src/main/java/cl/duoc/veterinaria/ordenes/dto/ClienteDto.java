package cl.duoc.veterinaria.ordenes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteDto(
        @Schema(example = "Pedro")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
        String nombre,

        @Schema(example = "Soto")
        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar 100 caracteres")
        String apellidos,

        @Schema(example = "pedro@correo.cl")
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Ingrese un correo válido")
        @Size(max = 100, message = "El correo no puede superar 100 caracteres")
        String correo) {
}
