package cl.duoc.veterinaria.usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;

/** Información que muestra Swagger en http://localhost:8081/swagger-ui.html */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI documentacion() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Usuarios - Veterinaria San Marcos")
                        .version("1.0")
                        .description("Registro, inicio de sesión con JWT, renovación de tokens y mantenedor de usuarios. "
                                + "Para probar los endpoints protegidos: ejecuta POST /api/auth/login, copia tokenAcceso "
                                + "y pégalo en el botón Authorize."))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
