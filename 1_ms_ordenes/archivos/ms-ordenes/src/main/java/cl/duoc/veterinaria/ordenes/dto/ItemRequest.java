package cl.duoc.veterinaria.ordenes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Solo se envía el producto y la cantidad: el precio lo pone el backend */
public record ItemRequest(
        @Schema(example = "1")
        @NotNull(message = "El producto es obligatorio")
        Long productoId,

        @Schema(example = "2")
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        @Max(value = 99, message = "La cantidad no puede superar 99")
        Integer cantidad) {
}
