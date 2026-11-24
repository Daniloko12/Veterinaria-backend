package cl.duoc.veterinaria.ordenes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record EstadoOrdenRequest(
        @Schema(example = "enviada", allowableValues = {"pagada", "enviada", "entregada"})
        @NotBlank(message = "Indique el nuevo estado")
        String estado) {
}
