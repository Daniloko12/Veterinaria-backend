package cl.duoc.veterinaria.ordenes.seguridad;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro que se ejecuta en cada petición: si trae la cabecera
 * "Authorization: Bearer &lt;token&gt;" y el token es válido, deja al usuario
 * autenticado con su rol (ROLE_ADMINISTRADOR, ROLE_CLIENTE, ...).
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String ATRIBUTO_ERROR_TOKEN = "errorToken";

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String cabecera = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            String token = cabecera.substring(7);
            try {
                UsuarioAutenticado usuario = jwtService.validar(token);
                List<SimpleGrantedAuthority> permisos = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.rol()));
                UsernamePasswordAuthenticationToken autenticacion =
                        new UsernamePasswordAuthenticationToken(usuario, null, permisos);
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            } catch (ExpiredJwtException e) {
                request.setAttribute(ATRIBUTO_ERROR_TOKEN, "El token expiró");
            } catch (JwtException | IllegalArgumentException e) {
                request.setAttribute(ATRIBUTO_ERROR_TOKEN, "El token no es válido");
            }
        }
        chain.doFilter(request, response);
    }
}
