package cl.duoc.veterinaria.usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * Microservicio de usuarios (puerto 8081).
 * Registra usuarios, inicia sesión entregando tokens JWT y permite al
 * administrador gestionar las cuentas.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class MsUsuariosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsUsuariosApplication.class, args);
    }
}
