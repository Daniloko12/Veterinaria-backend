package cl.duoc.veterinaria.usuarios.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.veterinaria.usuarios.seguridad.JwtAuthFilter;
import cl.duoc.veterinaria.usuarios.seguridad.JwtService;
import cl.duoc.veterinaria.usuarios.seguridad.RespuestasSeguridad;

/**
 * Reglas de acceso del microservicio de usuarios.
 * - Sin sesión: Swagger, iniciar sesión, registrarse, renovar el token y cerrar sesión.
 * - Con sesión: ver los datos propios (/api/auth/me).
 * - Solo ADMINISTRADOR: todo /api/usuarios (mantenedor de usuarios).
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http, JwtService jwtService, ObjectMapper mapper) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(reglas -> reglas
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/registro",
                        "/api/auth/refresh", "/api/auth/logout").permitAll()
                .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")
                .anyRequest().authenticated())
            .exceptionHandling(errores -> errores
                .authenticationEntryPoint(RespuestasSeguridad.sinAutenticacion(mapper))
                .accessDeniedHandler(RespuestasSeguridad.sinPermiso(mapper)))
            .addFilterBefore(new JwtAuthFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /** Las contraseñas se guardan cifradas con BCrypt, nunca en texto plano */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Permite que el frontend (Vite en el puerto 5173) llame a esta API */
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
