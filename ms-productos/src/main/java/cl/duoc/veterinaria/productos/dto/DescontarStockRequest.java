package cl.duoc.veterinaria.productos.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

/** Lo envía ms-ordenes cuando una compra se paga */
public record DescontarStockRequest(@NotEmpty(message = "Debe indicar al menos un producto") List<@Valid ItemStock> items) {
}
