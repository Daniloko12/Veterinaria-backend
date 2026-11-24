package cl.duoc.veterinaria.ordenes.config;

import java.time.Instant;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import cl.duoc.veterinaria.ordenes.modelo.DatosCliente;
import cl.duoc.veterinaria.ordenes.modelo.DetalleOrden;
import cl.duoc.veterinaria.ordenes.modelo.DireccionEntrega;
import cl.duoc.veterinaria.ordenes.modelo.EstadoOrden;
import cl.duoc.veterinaria.ordenes.modelo.Orden;
import cl.duoc.veterinaria.ordenes.modelo.PagoTarjeta;
import cl.duoc.veterinaria.ordenes.repositorio.OrdenRepository;

/** Si la tabla está vacía, carga 3 órdenes de ejemplo (2 pagadas y 1 rechazada) */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final OrdenRepository ordenes;

    public DatosIniciales(OrdenRepository ordenes) {
        this.ordenes = ordenes;
    }

    @Override
    public void run(String... args) {
        if (ordenes.count() > 0) {
            return;
        }
        // usuarioId 3 = Cliente Demo (id que le asigna ms-usuarios al crear los datos iniciales)
        Orden primera = orden("2026-09-15T14:30:00Z", new DatosCliente("Cliente", "Demo", "cliente@correo.cl"),
                new DireccionEntrega("Calle Los Aromos 456", "", "O'Higgins", "Rancagua", ""), 3L,
                EstadoOrden.ENTREGADA, "", new PagoTarjeta("Visa", "4242", "CLIENTE DEMO", "583201"));
        primera.agregarItem(new DetalleOrden(1L, "Alimento para perros", 15990, 1, "/img/alimento-perro.webp"));
        primera.agregarItem(new DetalleOrden(3L, "Correa para perros", 7990, 1, "/img/correa-perro.jpg"));
        guardar(primera);

        Orden segunda = orden("2026-09-28T18:10:00Z", new DatosCliente("Ana", "Rojas", "ana.rojas@correo.cl"),
                new DireccionEntrega("Los Crisantemos 89", "Depto 603", "Metropolitana de Santiago", "Providencia",
                        "Dejar con el conserje."), null,
                EstadoOrden.PAGADA, "", new PagoTarjeta("Mastercard", "4444", "ANA ROJAS", "104772"));
        segunda.agregarItem(new DetalleOrden(2L, "Juguete para gatos", 2392, 2, "/img/juguete-gato.webp"));
        segunda.agregarItem(new DetalleOrden(7L, "Shampoo hipoalergénico", 6291, 1, "/img/placeholder-higiene.svg"));
        guardar(segunda);

        Orden tercera = orden("2026-10-05T12:00:00Z", new DatosCliente("Cliente", "Demo", "cliente@correo.cl"),
                new DireccionEntrega("Calle Los Aromos 456", "", "O'Higgins", "Rancagua", ""), 3L,
                EstadoOrden.RECHAZADA, "La tarjeta fue rechazada por el banco emisor",
                new PagoTarjeta("Visa", "0002", "CLIENTE DEMO", ""));
        tercera.agregarItem(new DetalleOrden(9L, "Pipeta antiparasitaria", 9990, 1, "/img/placeholder-salud.svg"));
        guardar(tercera);
    }

    private Orden orden(String fecha, DatosCliente cliente, DireccionEntrega direccion, Long usuarioId,
                        EstadoOrden estado, String motivo, PagoTarjeta pago) {
        Orden orden = new Orden();
        orden.setFecha(Instant.parse(fecha));
        orden.setCliente(cliente);
        orden.setDireccion(direccion);
        orden.setUsuarioId(usuarioId);
        orden.setEstado(estado);
        orden.setMotivoRechazo(motivo);
        orden.setPago(pago);
        return orden;
    }

    private void guardar(Orden orden) {
        orden.setTotal(orden.calcularTotal());
        Orden guardada = ordenes.save(orden);
        guardada.setNumero(String.format("ORD-%05d", guardada.getId()));
        ordenes.save(guardada);
    }
}
