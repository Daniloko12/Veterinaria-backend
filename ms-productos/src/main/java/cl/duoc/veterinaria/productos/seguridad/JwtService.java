package cl.duoc.veterinaria.productos.seguridad;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Valida los tokens JWT que crea ms-usuarios (y el token de servicio de ms-ordenes).
 * Usa la misma clave secreta, así no necesita llamar a ms-usuarios en cada petición.
 */
@Service
public class JwtService {

    private final SecretKey clave;

    public JwtService(@Value("${jwt.secreto}") String secreto) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    /** Lanza JwtException si el token fue alterado o ya expiró */
    public UsuarioAutenticado validar(String token) {
        Claims datos = Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Number id = datos.get("id", Number.class);
        return new UsuarioAutenticado(id == null ? null : id.longValue(), datos.getSubject(),
                datos.get("rol", String.class));
    }
}
