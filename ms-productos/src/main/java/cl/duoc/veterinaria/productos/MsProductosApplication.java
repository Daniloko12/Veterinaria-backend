package cl.duoc.veterinaria.productos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * Microservicio de catálogo (puerto 8082).
 * Administra productos, categorías y el stock que descuentan las compras.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class MsProductosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsProductosApplication.class, args);
    }
}
