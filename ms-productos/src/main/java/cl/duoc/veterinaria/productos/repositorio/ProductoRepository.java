package cl.duoc.veterinaria.productos.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cl.duoc.veterinaria.productos.modelo.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    long countByCategoriaId(Long categoriaId);

    /** Productos cuyo stock llegó al mínimo definido */
    @Query("select p from Producto p where p.stock <= p.stockCritico order by p.stock asc")
    List<Producto> buscarCriticos();
}
