package cl.duoc.veterinaria.ordenes.modelo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/** Tabla "ordenes": cada compra, pagada o rechazada, con sus líneas en detalles_orden */
@Entity
@Table(name = "ordenes")
public class Orden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Número visible en la boleta, por ejemplo ORD-00004 */
    @Column(unique = true, length = 12)
    private String numero;

    @Column(nullable = false)
    private Instant fecha;

    @Embedded
    private DatosCliente cliente;

    @Embedded
    private DireccionEntrega direccion;

    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetalleOrden> items = new ArrayList<>();

    /** Total en pesos, con IVA incluido */
    @Column(nullable = false)
    private Integer total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoOrden estado;

    @Column(length = 200)
    private String motivoRechazo;

    /** Id del usuario en ms-usuarios; null si compró como invitado */
    private Long usuarioId;

    @Embedded
    private PagoTarjeta pago;

    public void agregarItem(DetalleOrden item) {
        item.setOrden(this);
        items.add(item);
    }

    public int calcularTotal() {
        return items.stream().mapToInt(DetalleOrden::subtotal).sum();
    }

    public Long getId() { return id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public Instant getFecha() { return fecha; }
    public void setFecha(Instant fecha) { this.fecha = fecha; }

    public DatosCliente getCliente() { return cliente; }
    public void setCliente(DatosCliente cliente) { this.cliente = cliente; }

    public DireccionEntrega getDireccion() { return direccion; }
    public void setDireccion(DireccionEntrega direccion) { this.direccion = direccion; }

    public List<DetalleOrden> getItems() { return items; }

    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }

    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public PagoTarjeta getPago() { return pago; }
    public void setPago(PagoTarjeta pago) { this.pago = pago; }
}
