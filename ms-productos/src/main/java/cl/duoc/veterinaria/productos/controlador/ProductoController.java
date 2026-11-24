package cl.duoc.veterinaria.productos.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.veterinaria.productos.dto.DescontarStockRequest;
import cl.duoc.veterinaria.productos.dto.ProductoRequest;
import cl.duoc.veterinaria.productos.dto.ProductoResponse;
import cl.duoc.veterinaria.productos.dto.StockRequest;
import cl.duoc.veterinaria.productos.servicio.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "CRUD del catálogo. Lectura pública; escritura solo administrador.")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Listar productos", description = "Filtros opcionales: categoría, texto y solo ofertas")
    public List<ProductoResponse> listar(
            @Parameter(description = "Id de la categoría") @RequestParam(required = false) Long categoriaId,
            @Parameter(description = "Texto a buscar en nombre, descripción o código") @RequestParam(required = false) String buscar,
            @Parameter(description = "true = solo productos con descuento") @RequestParam(required = false) Boolean ofertas) {
        return productoService.listar(categoriaId, buscar, ofertas);
    }

    @GetMapping("/criticos")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Productos con stock crítico", description = "Administrador y recepcionista")
    public List<ProductoResponse> criticos() {
        return productoService.criticos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un producto")
    public ProductoResponse obtener(@PathVariable Long id) {
        return productoService.obtener(id);
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear producto", description = "409 si el código ya existe; 404 si la categoría no existe")
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(datos));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar producto")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest datos) {
        return productoService.actualizar(id, datos);
    }

    @PatchMapping("/{id}/stock")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Reponer stock", description = "Administrador y recepcionista")
    public ProductoResponse actualizarStock(@PathVariable Long id, @Valid @RequestBody StockRequest datos) {
        return productoService.actualizarStock(id, datos.stock());
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar producto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/stock/descontar")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Descontar stock de una compra (uso interno)",
            description = "Lo llama ms-ordenes con un token de servicio. 409 si algún producto no tiene stock suficiente.")
    public List<ProductoResponse> descontarStock(@Valid @RequestBody DescontarStockRequest datos) {
        return productoService.descontarStock(datos.items());
    }
}
