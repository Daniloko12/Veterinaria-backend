package cl.duoc.veterinaria.ordenes.servicio;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.veterinaria.ordenes.dto.ItemRequest;
import cl.duoc.veterinaria.ordenes.dto.OrdenRequest;
import cl.duoc.veterinaria.ordenes.dto.OrdenResponse;
import cl.duoc.veterinaria.ordenes.dto.ProductoInfo;
import cl.duoc.veterinaria.ordenes.excepcion.AccesoDenegadoException;
import cl.duoc.veterinaria.ordenes.excepcion.ConflictoException;
import cl.duoc.veterinaria.ordenes.excepcion.RecursoNoEncontradoException;
import cl.duoc.veterinaria.ordenes.excepcion.SolicitudInvalidaException;
import cl.duoc.veterinaria.ordenes.excepcion.StockInsuficienteException;
import cl.duoc.veterinaria.ordenes.modelo.DatosCliente;
import cl.duoc.veterinaria.ordenes.modelo.DetalleOrden;
import cl.duoc.veterinaria.ordenes.modelo.DireccionEntrega;
import cl.duoc.veterinaria.ordenes.modelo.EstadoOrden;
import cl.duoc.veterinaria.ordenes.modelo.Orden;
import cl.duoc.veterinaria.ordenes.modelo.PagoTarjeta;
import cl.duoc.veterinaria.ordenes.repositorio.OrdenRepository;
import cl.duoc.veterinaria.ordenes.seguridad.UsuarioAutenticado;

/**
 * Lógica de negocio de las compras.
 *
 * Al crear una orden:
 * 1. Se piden a ms-productos el nombre, el precio final y el stock de cada producto
 *    (el precio nunca se toma del navegador, así nadie puede cambiarlo).
 * 2. Se revisa que haya stock, que la tarjeta no esté vencida y que el banco simulado la apruebe.
 * 3. Si todo está bien, ms-productos descuenta el stock y la orden queda PAGADA con un código
 *    de autorización. Si algo falla, la orden queda RECHAZADA con el motivo.
 * 4. Siempre se guarda la orden; de la tarjeta solo se guardan la marca y los últimos 4 dígitos.
 */
@Service
public class OrdenService {

    private static final List<String> ROLES_PERSONAL = List.of("ADMINISTRADOR", "RECEPCIONISTA");

    private final OrdenRepository ordenes;
    private final ProductosClient productosClient;
    private final PagoSimulado pagoSimulado;

    public OrdenService(OrdenRepository ordenes, ProductosClient productosClient, PagoSimulado pagoSimulado) {
        this.ordenes = ordenes;
        this.productosClient = productosClient;
        this.pagoSimulado = pagoSimulado;
    }

    @Transactional
    public OrdenResponse crear(OrdenRequest datos, UsuarioAutenticado usuario) {
        String numeroTarjeta = PagoSimulado.soloDigitos(datos.tarjeta().numero());
        if (numeroTarjeta.length() != 16) {
            throw new SolicitudInvalidaException("tarjeta.numero", "El número de tarjeta debe tener 16 dígitos");
        }
        if (!pagoSimulado.mesValido(datos.tarjeta().vencimiento())) {
            throw new SolicitudInvalidaException("tarjeta.vencimiento", "El mes de vencimiento no es válido");
        }

        Orden orden = new Orden();
        orden.setFecha(Instant.now());
        orden.setCliente(new DatosCliente(datos.cliente().nombre().trim(), datos.cliente().apellidos().trim(),
                datos.cliente().correo().trim().toLowerCase(Locale.ROOT)));
        orden.setDireccion(new DireccionEntrega(datos.direccion().calle().trim(), textoLimpio(datos.direccion().departamento()),
                datos.direccion().region(), datos.direccion().comuna(), textoLimpio(datos.direccion().indicaciones())));
        orden.setUsuarioId(usuario != null ? usuario.id() : null);

        // 1. Precios y stock desde ms-productos
        String productoSinStock = null;
        for (ItemRequest item : datos.items()) {
            ProductoInfo producto = productosClient.obtener(item.productoId());
            orden.agregarItem(new DetalleOrden(producto.id(), producto.nombre(), producto.precioFinal(),
                    item.cantidad(), producto.imagen()));
            if (productoSinStock == null && producto.stock() < item.cantidad()) {
                productoSinStock = producto.nombre();
            }
        }
        orden.setTotal(orden.calcularTotal());

        // 2. Reglas del pago
        String motivoRechazo = null;
        if (productoSinStock != null) {
            motivoRechazo = "No hay stock suficiente de " + productoSinStock;
        } else if (pagoSimulado.estaVencida(datos.tarjeta().vencimiento())) {
            motivoRechazo = "La tarjeta está vencida";
        } else if (pagoSimulado.esRechazada(numeroTarjeta)) {
            motivoRechazo = "La tarjeta fue rechazada por el banco emisor";
        } else {
            // 3. Pago aprobado: se descuenta el stock en ms-productos
            try {
                productosClient.descontarStock(datos.items());
            } catch (StockInsuficienteException e) {
                motivoRechazo = e.getMessage();
            }
        }

        boolean pagada = motivoRechazo == null;
        orden.setEstado(pagada ? EstadoOrden.PAGADA : EstadoOrden.RECHAZADA);
        orden.setMotivoRechazo(pagada ? "" : motivoRechazo);
        orden.setPago(new PagoTarjeta(
                pagoSimulado.detectarMarca(numeroTarjeta),
                pagoSimulado.ultimosCuatro(numeroTarjeta),
                datos.tarjeta().titular().trim().toUpperCase(Locale.ROOT),
                pagada ? pagoSimulado.generarCodigoAutorizacion() : ""));

        // 4. Se guarda y se le asigna el número de boleta
        Orden guardada = ordenes.save(orden);
        guardada.setNumero(String.format("ORD-%05d", guardada.getId()));
        return OrdenResponse.desde(ordenes.save(guardada));
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> listar() {
        return ordenes.findAll(Sort.by(Sort.Direction.DESC, "fecha")).stream().map(OrdenResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> delCliente(UsuarioAutenticado usuario) {
        return ordenes.buscarDelCliente(usuario.id(), usuario.correo()).stream().map(OrdenResponse::desde).toList();
    }

    /** El personal ve cualquier orden; un cliente solo las suyas */
    @Transactional(readOnly = true)
    public OrdenResponse obtener(Long id, UsuarioAutenticado usuario) {
        Orden orden = buscar(id);
        boolean esPersonal = ROLES_PERSONAL.contains(usuario.rol());
        boolean esDueno = (usuario.id() != null && usuario.id().equals(orden.getUsuarioId()))
                || orden.getCliente().getCorreo().equalsIgnoreCase(usuario.correo());
        if (!esPersonal && !esDueno) {
            throw new AccesoDenegadoException("No puedes ver la orden de otro cliente");
        }
        return OrdenResponse.desde(orden);
    }

    @Transactional
    public OrdenResponse cambiarEstado(Long id, String estadoTexto) {
        EstadoOrden nuevo = EstadoOrden.desdeTexto(estadoTexto);
        if (nuevo == null) {
            throw new SolicitudInvalidaException("estado", "El estado no existe");
        }
        if (nuevo == EstadoOrden.RECHAZADA) {
            throw new SolicitudInvalidaException("estado", "El estado rechazada solo lo asigna el pago");
        }
        Orden orden = buscar(id);
        if (orden.getEstado() == EstadoOrden.RECHAZADA) {
            throw new ConflictoException("Una orden rechazada no puede cambiar de estado");
        }
        orden.setEstado(nuevo);
        return OrdenResponse.desde(ordenes.save(orden));
    }

    @Transactional
    public void eliminar(Long id) {
        ordenes.delete(buscar(id));
    }

    private Orden buscar(Long id) {
        return ordenes.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la orden con id " + id));
    }

    private static String textoLimpio(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
