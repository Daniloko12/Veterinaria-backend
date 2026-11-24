package cl.duoc.veterinaria.productos.servicio;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.veterinaria.productos.dto.CategoriaRequest;
import cl.duoc.veterinaria.productos.dto.CategoriaResponse;
import cl.duoc.veterinaria.productos.excepcion.ConflictoException;
import cl.duoc.veterinaria.productos.excepcion.RecursoNoEncontradoException;
import cl.duoc.veterinaria.productos.modelo.Categoria;
import cl.duoc.veterinaria.productos.repositorio.CategoriaRepository;
import cl.duoc.veterinaria.productos.repositorio.ProductoRepository;

/**
 * Reglas de negocio de las categorías:
 * el nombre no se repite y no se puede eliminar una categoría que todavía tiene productos.
 */
@Service
public class CategoriaService {

    private final CategoriaRepository categorias;
    private final ProductoRepository productos;

    public CategoriaService(CategoriaRepository categorias, ProductoRepository productos) {
        this.categorias = categorias;
        this.productos = productos;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categorias.findAll(Sort.by("id")).stream().map(this::aRespuesta).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Long id) {
        return aRespuesta(buscar(id));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest datos) {
        if (categorias.existsByNombreIgnoreCase(datos.nombre().trim())) {
            throw new ConflictoException("nombre", "Ya existe una categoría con ese nombre");
        }
        Categoria nueva = new Categoria(datos.nombre().trim(), datos.descripcion().trim(), datos.imagen());
        return aRespuesta(categorias.save(nueva));
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest datos) {
        Categoria categoria = buscar(id);
        if (categorias.existsByNombreIgnoreCaseAndIdNot(datos.nombre().trim(), id)) {
            throw new ConflictoException("nombre", "Ya existe una categoría con ese nombre");
        }
        categoria.setNombre(datos.nombre().trim());
        categoria.setDescripcion(datos.descripcion().trim());
        categoria.setImagen(datos.imagen());
        return aRespuesta(categorias.save(categoria));
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscar(id);
        long cantidad = productos.countByCategoriaId(id);
        if (cantidad > 0) {
            throw new ConflictoException("No se puede eliminar: la categoría tiene " + cantidad + " producto(s)");
        }
        categorias.delete(categoria);
    }

    @Transactional(readOnly = true)
    public Categoria buscar(Long id) {
        return categorias.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la categoría con id " + id));
    }

    private CategoriaResponse aRespuesta(Categoria categoria) {
        return CategoriaResponse.desde(categoria, productos.countByCategoriaId(categoria.getId()));
    }
}
