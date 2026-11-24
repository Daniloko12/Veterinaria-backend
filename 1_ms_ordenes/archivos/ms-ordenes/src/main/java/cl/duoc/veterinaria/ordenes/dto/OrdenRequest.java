package cl.duoc.veterinaria.ordenes.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/** Lo que envía el checkout del frontend */
public record OrdenRequest(
        @NotNull(message = "Faltan los datos del cliente") @Valid ClienteDto cliente,
        @NotNull(message = "Falta la dirección de entrega") @Valid DireccionDto direccion,
        @NotEmpty(message = "El carrito está vacío") List<@Valid ItemRequest> items,
        @NotNull(message = "Faltan los datos de la tarjeta") @Valid TarjetaRequest tarjeta) {
}
