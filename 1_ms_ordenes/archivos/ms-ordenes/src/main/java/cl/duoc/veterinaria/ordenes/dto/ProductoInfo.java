package cl.duoc.veterinaria.ordenes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Parte de la respuesta de ms-productos que necesita una compra */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductoInfo(Long id, String nombre, Integer precioFinal, Integer stock, String imagen) {
}
