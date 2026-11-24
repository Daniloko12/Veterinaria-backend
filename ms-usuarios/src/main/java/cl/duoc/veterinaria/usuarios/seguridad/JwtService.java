package cl.duoc.veterinaria.usuarios.seguridad;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cl.duoc.veterinaria.usuarios.modelo.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Crea y valida los tokens JWT.
 * El token de acceso dura pocos minutos y lleva el id, el correo y el rol del usuario.
 * Se firma con HMAC-SHA256 usando una clave secreta que comparten los 3 microservicios,
 * así cada uno puede verificar el token sin consultar a ms-usuarios.
 */
@Service
public class JwtService {

    private final SecretKey clave;
    private final long minutosAcceso;

    public JwtService(@Value("${jwt.secreto}") String secreto,
                      @Value("${jwt.minutos-acceso}") long minutosAcceso) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.minutosAcceso = minutosAcceso;
    }

    public String generarTokenAcceso(Usuario usuario) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(usuario.getCorreo())
                .issuer("ms-usuarios")
                .claim("id", usuario.getId())
                .claim("rol", usuario.getRol().name())
                .claim("nombre", usuario.getNombre())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(minutosAcceso, ChronoUnit.MINUTES)))
                .signWith(clave)
                .compact();
    }

    /** Segundos que dura el token de acceso (el frontend los usa para renovarlo a tiempo) */
    public long segundosDeVida() {
        return minutosAcceso * 60;
    }

    /**
     * Verifica la firma y la fecha de expiración del token.
     * Lanza JwtException si el token fue alterado o ya expiró.
     */
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
