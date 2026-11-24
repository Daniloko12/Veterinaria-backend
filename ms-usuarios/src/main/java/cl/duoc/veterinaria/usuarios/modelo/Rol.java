package cl.duoc.veterinaria.usuarios.modelo;

import java.util.Locale;

/**
 * Roles del sistema:
 * - ADMINISTRADOR: administra productos, categorías, usuarios y órdenes.
 * - RECEPCIONISTA: revisa órdenes y repone stock, sin administrar usuarios.
 * - CLIENTE: compra en la tienda y revisa sus propias compras.
 */
public enum Rol {
    ADMINISTRADOR,
    RECEPCIONISTA,
    CLIENTE;

    /** Texto que usa el frontend: "administrador", "recepcionista" o "cliente" */
    public String aTexto() {
        return name().toLowerCase(Locale.ROOT);
    }

    /** Convierte el texto del frontend al rol; devuelve null si no existe */
    public static Rol desdeTexto(String texto) {
        if (texto == null) {
            return null;
        }
        for (Rol rol : values()) {
            if (rol.name().equalsIgnoreCase(texto.trim())) {
                return rol;
            }
        }
        return null;
    }
}
