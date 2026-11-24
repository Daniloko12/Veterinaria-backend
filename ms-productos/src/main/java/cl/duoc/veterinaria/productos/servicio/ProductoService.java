package cl.duoc.veterinaria.productos.servicio;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.veterinaria.productos.dto.ItemStock;
import cl.duoc.veterinaria.productos.dto.ProductoRequest;
import cl.duoc.veterinaria.productos.dto.ProductoResponse;
import cl.duoc.veterinaria.productos.excepcion.ConflictoException;
import cl.duoc.veterinaria.productos.excepcion.RecursoNoEncontradoException;
import cl.duoc.veterinaria.productos.modelo.Producto;
import cl.duoc.veterinaria.productos.repositorio.ProductoRepository;

/**
 * Reglas de negocio del catálogo:
 * - El código del producto es único (se guarda en mayúsculas).
 * - El producto debe pertenecer a una categoría existente.
 * - El precio final aplica el descuento.
 * - Al pagar una compra el stock se descuenta en una sola transacción:
 *   si un producto no alcanza, no se descuenta ninguno.
 */
@Service
public class ProductoService {

    private final ProductoRepository productos;
    private final CategoriaService categoriaService;

    public ProductoService(ProductoRepository productos, CategoriaService categoriaService) {
        this.productos = productos;
        this.categoriaService = categoriaService;
    }

    /** Lista con filtros opcionales: categoría, texto (nombre, descripción o código) y solo ofertas */
    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(Long categoriaId, String buscar, Boolean soloOfertas) {
        String texto = normalizar(buscar);
        return productos.findAll(Sort.by("id")).stream()
                .filter(p -> categoriaId == null || p.getCategoria().getId().equals(categoriaId))
                .filter(p -> !Boolean.TRUE.equals(soloOfertas) || p.getDescuento() > 0)
                .filter(p -> texto.isEmpty()
                        || normalizar(p.getNombre()).contains(texto)
                        || normalizar(p.getDescripcion()).contains(texto)
                        || normalizar(p.getCodigo()).contains(texto))
                .map(ProductoResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return ProductoResponse.desde(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> criticos() {
        return productos.buscarCriticos().stream().map(ProductoResponse::desde).toList();
    }

    @Transactional
    public ProductoResponse crear(ProductoRequest datos) {
        String codigo = datos.codigo().trim().toUpperCase(Locale.ROOT);
        if (productos.existsByCodigoIgnoreCase(codigo)) {
            throw new ConflictoException("codigo", "Ya existe un producto con ese código");
        }
        Producto producto = new Producto();
        copiarDatos(datos, producto);
        return ProductoResponse.desde(productos.save(producto));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest datos) {
        Producto producto = buscar(id);
        String codigo = datos.codigo().trim().toUpperCase(Locale.ROOT);
        if (productos.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
            throw new ConflictoException("codigo", "Ya existe un producto con ese código");
        }
        copiarDatos(datos, producto);
        return ProductoResponse.desde(productos.save(producto));
    }

    @Transactional
    public ProductoResponse actualizarStock(Long id, int stock) {
        Producto producto = buscar(id);
        producto.setStock(stock);
        return ProductoResponse.desde(productos.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        productos.delete(buscar(id));
    }

    /**
     * Descuenta el stock de una compra. Primero revisa todos los productos y,
     * solo si todos alcanzan, descuenta. Si algo falla, la transacción se revierte.
     */
    @Transactional
    public List<ProductoResponse> descontarStock(List<ItemStock> items) {
        Map<Long, Integer> cantidades = new HashMap<>();
        items.forEach(item -> cantidades.merge(item.productoId(), item.cantidad(), Integer::sum));

        List<Producto> afectados = cantidades.keySet().stream().map(this::buscar).toList();
        for (Producto producto : afectados) {
            int pedido = cantidades.get(producto.getId());
            if (producto.getStock() < pedido) {
                throw new ConflictoException("No hay stock suficiente de " + producto.getNombre());
            }
        }
        afectados.forEach(producto -> producto.setStock(producto.getStock() - cantidades.get(producto.getId())));
        return productos.saveAll(afectados).stream().map(ProductoResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public Producto buscar(Long id) {
        return productos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id));
    }

    private void copiarDatos(ProductoRequest datos, Producto producto) {
        producto.setCodigo(datos.codigo().trim().toUpperCase(Locale.ROOT));
        producto.setNombre(datos.nombre().trim());
        producto.setDescripcion(datos.descripcion() == null ? "" : datos.descripcion().trim());
        producto.setPrecio(datos.precio());
        producto.setDescuento(datos.descuento());
        producto.setStock(datos.stock());
        producto.setStockCritico(datos.stockCritico());
        producto.setCategoria(categoriaService.buscar(datos.categoriaId()));
        producto.setImagen(datos.imagen());
    }

    /** Quita tildes y pasa a minúsculas para comparar textos (igual que el filtro del frontend) */
    private static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
