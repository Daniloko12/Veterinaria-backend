package cl.duoc.veterinaria.ordenes.seguridad;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Valida los tokens de los usuarios (creados por ms-usuarios) y crea el
 * token de servicio con el que ms-ordenes le pide a ms-productos descontar stock.
 */
@Service
public class JwtService {

    private final SecretKey clave;

    public JwtService(@Value("${jwt.secreto}") String secreto) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

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

    /** Token de 1 minuto con rol SERVICIO: solo sirve para la comunicación entre microservicios */
    public String generarTokenServicio() {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject("ms-ordenes")
                .issuer("ms-ordenes")
                .claim("rol", "SERVICIO")
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(1, ChronoUnit.MINUTES)))
                .signWith(clave)
                .compact();
    }
}
