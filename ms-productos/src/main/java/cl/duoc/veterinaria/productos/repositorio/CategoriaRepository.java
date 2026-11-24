package cl.duoc.veterinaria.productos.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.veterinaria.productos.modelo.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
