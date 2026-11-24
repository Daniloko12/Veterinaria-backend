package cl.duoc.veterinaria.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductoRequest(
        @Schema(example = "JUG-003")
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 20, message = "El código no puede superar 20 caracteres")
        String codigo,

        @Schema(example = "Rascador para gatos")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @Schema(example = "Rascador de cartón con hierba gatera.")
        @Size(max = 300, message = "La descripción no puede superar 300 caracteres")
        String descripcion,

        @Schema(example = "5990")
        @NotNull(message = "El precio es obligatorio")
        @Min(value = 1, message = "El precio debe ser mayor o igual a 1")
        Integer precio,

        @Schema(example = "10")
        @NotNull(message = "El descuento es obligatorio")
        @Min(value = 0, message = "El descuento debe ser mayor o igual a 0")
        @Max(value = 90, message = "El descuento no puede ser mayor a 90")
        Integer descuento,

        @Schema(example = "15")
        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
        Integer stock,

        @Schema(example = "5")
        @NotNull(message = "El stock crítico es obligatorio")
        @Min(value = 0, message = "El stock crítico debe ser mayor o igual a 0")
        Integer stockCritico,

        @Schema(example = "2")
        @NotNull(message = "Seleccione una categoría")
        Long categoriaId,

        @Schema(example = "/img/placeholder-juguetes.svg")
        @NotBlank(message = "Seleccione una imagen")
        String imagen) {
}
