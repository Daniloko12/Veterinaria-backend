package cl.duoc.veterinaria.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * API Gateway: el frontend llama solo a http://localhost:8080 y el gateway reenvía cada
 * petición al microservicio correcto, buscándolo por su nombre en Eureka.
 * Las rutas están en application.yml.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
