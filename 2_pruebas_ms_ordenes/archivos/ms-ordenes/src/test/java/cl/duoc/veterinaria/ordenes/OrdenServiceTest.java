package cl.duoc.veterinaria.ordenes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import cl.duoc.veterinaria.ordenes.dto.ClienteDto;
import cl.duoc.veterinaria.ordenes.dto.DireccionDto;
import cl.duoc.veterinaria.ordenes.dto.ItemRequest;
import cl.duoc.veterinaria.ordenes.dto.OrdenRequest;
import cl.duoc.veterinaria.ordenes.dto.OrdenResponse;
import cl.duoc.veterinaria.ordenes.dto.ProductoInfo;
import cl.duoc.veterinaria.ordenes.dto.TarjetaRequest;
import cl.duoc.veterinaria.ordenes.modelo.Orden;
import cl.duoc.veterinaria.ordenes.repositorio.OrdenRepository;
import cl.duoc.veterinaria.ordenes.servicio.OrdenService;
import cl.duoc.veterinaria.ordenes.servicio.PagoSimulado;
import cl.duoc.veterinaria.ordenes.servicio.ProductosClient;

/**
 * Pruebas de la lógica de compra. La base de datos y ms-productos se reemplazan
 * por mocks de Mockito, así la prueba no necesita MySQL ni los otros servicios.
 */
class OrdenServiceTest {

    private final OrdenRepository ordenes = mock(OrdenRepository.class);
    private final ProductosClient productos = mock(ProductosClient.class);
    private final OrdenService servicio = new OrdenService(ordenes, productos, new PagoSimulado());

    @BeforeEach
    void configurar() {
        // Al guardar, la "base de datos" le pone el id 4 a la orden
        when(ordenes.save(any(Orden.class))).thenAnswer(invocacion -> {
            Orden orden = invocacion.getArgument(0);
            ReflectionTestUtils.setField(orden, "id", 4L);
            return orden;
        });
        when(productos.obtener(1L)).thenReturn(
                new ProductoInfo(1L, "Alimento para perros", 15990, 20, "/img/alimento-perro.webp"));
    }

    private OrdenRequest pedido(String tarjeta, int cantidad) {
        return new OrdenRequest(
                new ClienteDto("Pedro", "Soto", "pedro@correo.cl"),
                new DireccionDto("Los Crisantemos 123", "", "O'Higgins", "Rancagua", ""),
                List.of(new ItemRequest(1L, cantidad)),
                new TarjetaRequest(tarjeta, "Pedro Soto", "12/30", "123"));
    }

    @Test
    void conTarjetaAprobadaLaOrdenQuedaPagadaYSeDescuentaElStock() {
        OrdenResponse orden = servicio.crear(pedido("4242 4242 4242 4242", 2), null);

        assertEquals("pagada", orden.estado());
        assertEquals("ORD-00004", orden.numero());
        assertEquals(31980, orden.total().intValue());
        assertEquals("Visa", orden.pago().marca());
        assertEquals("4242", orden.pago().ultimos4());
        verify(productos).descontarStock(anyList());
    }

    @Test
    void conLaTarjetaDeRechazoLaOrdenQuedaRechazadaYNoSeTocaElStock() {
        OrdenResponse orden = servicio.crear(pedido("4000 0000 0000 0002", 1), null);

        assertEquals("rechazada", orden.estado());
        assertEquals("La tarjeta fue rechazada por el banco emisor", orden.motivoRechazo());
        assertEquals("", orden.pago().codigoAutorizacion());
        verify(productos, never()).descontarStock(anyList());
    }

    @Test
    void sinStockSuficienteLaOrdenQuedaRechazada() {
        OrdenResponse orden = servicio.crear(pedido("4242 4242 4242 4242", 25), null);

        assertEquals("rechazada", orden.estado());
        assertEquals("No hay stock suficiente de Alimento para perros", orden.motivoRechazo());
        verify(productos, never()).descontarStock(anyList());
    }
}
