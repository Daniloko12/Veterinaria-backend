package cl.duoc.veterinaria.ordenes.servicio;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.veterinaria.ordenes.dto.ItemRequest;
import cl.duoc.veterinaria.ordenes.dto.ProductoInfo;
import cl.duoc.veterinaria.ordenes.excepcion.ErrorRespuesta;
import cl.duoc.veterinaria.ordenes.excepcion.ServicioNoDisponibleException;
import cl.duoc.veterinaria.ordenes.excepcion.SolicitudInvalidaException;
import cl.duoc.veterinaria.ordenes.excepcion.StockInsuficienteException;
import cl.duoc.veterinaria.ordenes.seguridad.JwtService;

/**
 * Comunicación entre microservicios: ms-ordenes llama a la API REST de ms-productos.
 * La dirección se obtiene de Eureka por el nombre "ms-productos" (ver ClienteHttpConfig).
 * - GET  /api/productos/{id}            para obtener el precio final y el stock (público).
 * - POST /api/productos/stock/descontar para descontar el stock (con token de servicio).
 */
@Component
public class ProductosClient {

    private final RestClient cliente;
    private final JwtService jwtService;
    private final ObjectMapper mapper;

    public ProductosClient(RestClient.Builder builder, @Value("${servicios.productos.url}") String urlProductos,
                           JwtService jwtService, ObjectMapper mapper) {
        this.cliente = builder.baseUrl(urlProductos).build();
        this.jwtService = jwtService;
        this.mapper = mapper;
    }

    public ProductoInfo obtener(Long productoId) {
        try {
            ProductoInfo producto = cliente.get()
                    .uri("/api/productos/{id}", productoId)
                    .retrieve()
                    .body(ProductoInfo.class);
            if (producto == null) {
                throw new SolicitudInvalidaException("items", "El producto " + productoId + " no existe");
            }
            return producto;
        } catch (HttpClientErrorException.NotFound e) {
            throw new SolicitudInvalidaException("items", "El producto " + productoId + " no existe");
        } catch (ResourceAccessException | IllegalStateException e) {
            // ResourceAccessException: no responde. IllegalStateException: no está registrado en Eureka
            throw new ServicioNoDisponibleException("El servicio de productos no está disponible. Intenta más tarde.");
        }
    }

    /** Lanza StockInsuficienteException si ms-productos responde 409 */
    public void descontarStock(List<ItemRequest> items) {
        try {
            cliente.post()
                    .uri("/api/productos/stock/descontar")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtService.generarTokenServicio())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("items", items))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict e) {
            throw new StockInsuficienteException(leerMensaje(e.getResponseBodyAsString(), "No hay stock suficiente"));
        } catch (ResourceAccessException | IllegalStateException e) {
            // ResourceAccessException: no responde. IllegalStateException: no está registrado en Eureka
            throw new ServicioNoDisponibleException("El servicio de productos no está disponible. Intenta más tarde.");
        }
    }

    private String leerMensaje(String cuerpo, String porDefecto) {
        try {
            ErrorRespuesta error = mapper.readValue(cuerpo, ErrorRespuesta.class);
            return error.mensaje() == null ? porDefecto : error.mensaje();
        } catch (Exception e) {
            return porDefecto;
        }
    }
}
