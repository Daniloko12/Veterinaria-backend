package cl.duoc.veterinaria.usuarios.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.veterinaria.usuarios.modelo.Usuario;

/** Acceso a la tabla usuarios. Spring Data genera las consultas a partir del nombre del método. */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByRunIgnoreCase(String run);

    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Long id);

    boolean existsByRunIgnoreCaseAndIdNot(String run, Long id);
}
