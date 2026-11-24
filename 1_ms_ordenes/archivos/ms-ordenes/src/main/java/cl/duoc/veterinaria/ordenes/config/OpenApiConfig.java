package cl.duoc.veterinaria.ordenes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

/** Información que muestra Swagger en http://localhost:8083/swagger-ui.html */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI documentacion() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Órdenes - Veterinaria San Marcos")
                        .version("1.0")
                        .description("Compras con pago simulado con tarjeta. Comprar no requiere sesión; ver y "
                                + "administrar órdenes requiere un token (obtenlo en ms-usuarios con "
                                + "POST /api/auth/login y pégalo en Authorize). Tarjeta de rechazo: 4000 0000 0000 0002."))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
