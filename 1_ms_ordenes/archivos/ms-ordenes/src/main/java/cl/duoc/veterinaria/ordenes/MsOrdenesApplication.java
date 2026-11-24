package cl.duoc.veterinaria.ordenes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * Microservicio de órdenes (puerto 8083).
 * Procesa las compras: consulta precios y stock en ms-productos, simula el pago
 * con tarjeta, descuenta el stock y guarda la boleta.
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class MsOrdenesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsOrdenesApplication.class, args);
    }
}
