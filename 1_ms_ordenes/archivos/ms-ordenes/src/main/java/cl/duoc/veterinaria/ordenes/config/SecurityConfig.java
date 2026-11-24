package cl.duoc.veterinaria.ordenes.config;

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

import cl.duoc.veterinaria.ordenes.seguridad.JwtAuthFilter;
import cl.duoc.veterinaria.ordenes.seguridad.JwtService;
import cl.duoc.veterinaria.ordenes.seguridad.RespuestasSeguridad;

/**
 * Reglas de acceso de las órdenes:
 * - Cualquiera puede comprar (también como invitado).
 * - Con sesión: ver las compras propias y el detalle de una orden propia.
 * - ADMINISTRADOR y RECEPCIONISTA: listar todas las órdenes y cambiar su estado.
 * - Solo ADMINISTRADOR: eliminar órdenes.
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
                .requestMatchers(HttpMethod.POST, "/api/ordenes").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/ordenes/mis-ordenes").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/ordenes").hasAnyRole(ADMIN, RECEPCION)
                .requestMatchers(HttpMethod.GET, "/api/ordenes/*").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/ordenes/*/estado").hasAnyRole(ADMIN, RECEPCION)
                .requestMatchers(HttpMethod.DELETE, "/api/ordenes/*").hasRole(ADMIN)
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
