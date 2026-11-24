package cl.duoc.veterinaria.productos.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.veterinaria.productos.seguridad.JwtAuthFilter;
import cl.duoc.veterinaria.productos.seguridad.JwtService;
import cl.duoc.veterinaria.productos.seguridad.RespuestasSeguridad;

/**
 * Reglas de acceso del catálogo:
 * - Cualquiera puede ver productos y categorías (la tienda es pública).
 * - ADMINISTRADOR y RECEPCIONISTA ven los productos con stock crítico y pueden reponer stock.
 * - Solo ADMINISTRADOR crea, edita y elimina productos y categorías.
 * - Solo el rol SERVICIO (ms-ordenes) descuenta stock al pagar una compra.
 */
@Configuration
public class SecurityConfig {

    private static final String ADMIN = "ADMINISTRADOR";
    private static final String RECEPCION = "RECEPCIONISTA";

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http, JwtService jwtService, ObjectMapper mapper) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(reglas -> reglas
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/productos/criticos").hasAnyRole(ADMIN, RECEPCION)
                .requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/categorias/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/productos/stock/descontar").hasRole("SERVICIO")
                .requestMatchers(HttpMethod.PATCH, "/api/productos/*/stock").hasAnyRole(ADMIN, RECEPCION)
                .requestMatchers("/api/productos/**", "/api/categorias/**").hasRole(ADMIN)
                .anyRequest().authenticated())
            .exceptionHandling(errores -> errores
                .authenticationEntryPoint(RespuestasSeguridad.sinAutenticacion(mapper))
                .accessDeniedHandler(RespuestasSeguridad.sinPermiso(mapper)))
            .addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origenes}") List<String> origenes) {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(origenes);
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", cors);
        return fuente;
    }
}
