package cl.duoc.veterinaria.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos del formulario de usuario del panel de administración */
public record UsuarioRequest(
        @Schema(example = "176543213")
        @NotBlank(message = "El RUN es obligatorio")
        @Pattern(regexp = "^\\d{7,8}[0-9Kk]$", message = "Ingrese el RUN sin puntos ni guion")
        String run,

        @Schema(example = "Camila")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
        String nombre,

        @Schema(example = "Pérez Lagos")
        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 100, message = "Los apellidos no pueden superar 100 caracteres")
        String apellidos,

        @Schema(example = "camila@veterinaria.cl")
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Ingrese un correo válido")
        @Size(max = 100, message = "El correo no puede superar 100 caracteres")
        String correo,

        @Schema(example = "clave123", description = "Obligatoria al crear. Al editar, vacía = mantener la actual")
        String clave,

        @Schema(example = "1995-03-10")
        @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}$", message = "Use el formato AAAA-MM-DD")
        String fechaNacimiento,

        @Schema(example = "recepcionista", allowableValues = {"administrador", "recepcionista", "cliente"})
        @NotBlank(message = "Seleccione un rol")
        String rol,

        @Schema(example = "O'Higgins")
        @NotBlank(message = "Seleccione una región")
        String region,

        @Schema(example = "Rancagua")
        @NotBlank(message = "Seleccione una comuna")
        String comuna,

        @Schema(example = "Av. Brasil 456")
        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 300, message = "La dirección no puede superar 300 caracteres")
        String direccion,

        @Schema(example = "true")
        Boolean activo) {
}
