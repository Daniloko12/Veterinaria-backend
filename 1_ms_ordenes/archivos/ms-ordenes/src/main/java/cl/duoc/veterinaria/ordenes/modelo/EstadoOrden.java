package cl.duoc.veterinaria.ordenes.modelo;

import java.util.Locale;

/**
 * Estados de una orden:
 * PAGADA o RECHAZADA los asigna el pago; después el personal la marca ENVIADA y ENTREGADA.
 */
public enum EstadoOrden {
    PAGADA,
    RECHAZADA,
    ENVIADA,
    ENTREGADA;

    public String aTexto() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static EstadoOrden desdeTexto(String texto) {
        if (texto == null) {
            return null;
        }
        for (EstadoOrden estado : values()) {
            if (estado.name().equalsIgnoreCase(texto.trim())) {
                return estado;
            }
        }
        return null;
    }
}
