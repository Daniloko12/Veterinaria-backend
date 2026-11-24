package cl.duoc.veterinaria.ordenes.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Una línea de la boleta. Guarda el nombre y el precio del momento de la compra,
 * así la boleta no cambia aunque después cambie el producto.
 */
@Entity
@Table(name = "detalles_orden")
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "orden_id")
    private Orden orden;

    /** Id del producto en ms-productos (otra base de datos, por eso no es una relación JPA) */
    @Column(nullable = false)
    private Long productoId;

    @Column(nullable = false, length = 100)
    private String nombre;

    /** Precio unitario final (con descuento) */
    @Column(nullable = false)
    private Integer precio;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(length = 200)
    private String imagen;

    protected DetalleOrden() {
    }

    public DetalleOrden(Long productoId, String nombre, Integer precio, Integer cantidad, String imagen) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imagen = imagen;
    }

    public int subtotal() {
        return precio * cantidad;
    }

    public Long getId() { return id; }
    public Orden getOrden() { return orden; }
    void setOrden(Orden orden) { this.orden = orden; }
    public Long getProductoId() { return productoId; }
    public String getNombre() { return nombre; }
    public Integer getPrecio() { return precio; }
    public Integer getCantidad() { return cantidad; }
    public String getImagen() { return imagen; }
}
