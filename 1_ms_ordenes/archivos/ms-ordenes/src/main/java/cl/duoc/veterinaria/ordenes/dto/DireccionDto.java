package cl.duoc.veterinaria.ordenes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DireccionDto(
        @Schema(example = "Los Crisantemos 123")
        @NotBlank(message = "La calle es obligatoria")
        @Size(max = 150, message = "La calle no puede superar 150 caracteres")
        String calle,

        @Schema(example = "Depto 12")
        @Size(max = 50, message = "Máximo 50 caracteres")
        String departamento,

        @Schema(example = "O'Higgins")
        @NotBlank(message = "Seleccione una región")
        String region,

        @Schema(example = "Rancagua")
        @NotBlank(message = "Seleccione una comuna")
        String comuna,

        @Schema(example = "Tocar el timbre")
        @Size(max = 300, message = "Máximo 300 caracteres")
        String indicaciones) {
}
