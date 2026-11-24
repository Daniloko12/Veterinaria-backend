package cl.duoc.veterinaria.usuarios.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.veterinaria.usuarios.modelo.TokenRenovacion;
import cl.duoc.veterinaria.usuarios.modelo.Usuario;

public interface TokenRenovacionRepository extends JpaRepository<TokenRenovacion, Long> {

    Optional<TokenRenovacion> findByToken(String token);

    void deleteByUsuario(Usuario usuario);
}
