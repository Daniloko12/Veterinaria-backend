package cl.duoc.veterinaria.ordenes.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos de la tarjeta. Se usan para el pago simulado y no se guardan completos. */
public record TarjetaRequest(
        @Schema(example = "4242 4242 4242 4242", description = "Usa 4000 0000 0000 0002 para simular un rechazo")
        @NotBlank(message = "El número de tarjeta es obligatorio")
        @Pattern(regexp = "^[0-9 ]{16,19}$", message = "El número de tarjeta debe tener 16 dígitos")
        String numero,

        @Schema(example = "Pedro Soto")
        @NotBlank(message = "El nombre del titular es obligatorio")
        @Size(max = 60, message = "El nombre del titular no puede superar 60 caracteres")
        String titular,

        @Schema(example = "12/30")
        @NotBlank(message = "La fecha de vencimiento es obligatoria")
        @Pattern(regexp = "^\\d{2}/\\d{2}$", message = "Use el formato MM/AA")
        String vencimiento,

        @Schema(example = "123")
        @NotBlank(message = "El CVV es obligatorio")
        @Pattern(regexp = "^\\d{3}$", message = "El CVV debe tener 3 dígitos")
        String cvv) {
}
