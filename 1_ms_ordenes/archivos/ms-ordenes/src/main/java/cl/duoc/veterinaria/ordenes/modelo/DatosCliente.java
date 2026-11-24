package cl.duoc.veterinaria.ordenes.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Datos de quien compra (se guardan como columnas de la tabla ordenes) */
@Embeddable
public class DatosCliente {

    @Column(name = "cliente_nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "cliente_apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "cliente_correo", nullable = false, length = 100)
    private String correo;

    protected DatosCliente() {
    }

    public DatosCliente(String nombre, String apellidos, String correo) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
    }

    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getCorreo() { return correo; }
}
