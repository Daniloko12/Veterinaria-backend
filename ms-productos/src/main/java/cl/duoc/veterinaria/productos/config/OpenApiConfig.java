package cl.duoc.veterinaria.productos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

/** Información que muestra Swagger en http://localhost:8082/swagger-ui.html */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI documentacion() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Productos - Veterinaria San Marcos")
                        .version("1.0")
                        .description("Catálogo de productos y categorías. La lectura es pública; crear, editar y "
                                + "eliminar requiere un token de administrador (obtenlo en ms-usuarios con "
                                + "POST /api/auth/login y pégalo en Authorize)."))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
