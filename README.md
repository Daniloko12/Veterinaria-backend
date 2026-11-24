# Veterinaria San Marcos · Backend

Backend de la tienda Veterinaria San Marcos (DSY1104, Evaluación Parcial 3).
Son **3 microservicios Spring Boot**, cada uno con su propia base de datos MySQL, más un **API Gateway** y un servidor **Eureka** (Spring Cloud).

| Módulo | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| eureka-server | 8761 | — | Registro de servicios: cada servicio se anota con su nombre, dirección y puerto |
| api-gateway | 8080 | — | Puerta de entrada única para el frontend: envía cada petición al microservicio que corresponde |
| ms-usuarios | 8081 | vsm_usuarios | Login con JWT, registro, renovación de tokens, CRUD de usuarios y roles |
| ms-productos | 8082 | vsm_productos | CRUD de productos y categorías, ofertas, stock crítico |
| ms-ordenes | 8083 | vsm_ordenes | Compra, pago simulado, boleta y estado del despacho |

Rutas del gateway (`api-gateway/src/main/resources/application.yml`):

| Ruta | Microservicio |
|---|---|
| `/api/auth/**`, `/api/usuarios/**` | `lb://ms-usuarios` |
| `/api/productos/**`, `/api/categorias/**` | `lb://ms-productos` |
| `/api/ordenes/**` | `lb://ms-ordenes` |

`lb://` significa que el gateway busca el servicio por su nombre en Eureka. `ms-ordenes` también llama a `ms-productos` por su nombre (`http://ms-productos`) para obtener los precios y descontar el stock.

## Requisitos

- Java 17 o 21
- Visual Studio Code con las extensiones **Extension Pack for Java** y **Spring Boot Extension Pack**
- Laragon con MySQL encendido en el puerto 3306 (usuario `root` sin contraseña)

## Cómo ejecutar

1. En Laragon presionar **Iniciar todo**. Las bases de datos se crean solas al iniciar cada microservicio.
2. Abrir esta carpeta en VS Code y esperar **Java: Ready**.
3. En **Spring Boot Dashboard**, iniciar en este orden:
   1. `eureka-server`
   2. `ms-usuarios`, `ms-productos` y `ms-ordenes`
   3. `api-gateway`
4. Comprobar:
   - Panel de Eureka: http://localhost:8761 (deben aparecer los 4 servicios registrados)
   - API por el gateway: http://localhost:8080/api/productos
   - Swagger unificado: http://localhost:8080/swagger-ui.html
   - Swagger de cada microservicio: http://localhost:8081/swagger-ui.html, http://localhost:8082/swagger-ui.html, http://localhost:8083/swagger-ui.html

Si MySQL tiene contraseña, cambiar `spring.datasource.password` en el `application.properties` de cada microservicio.

## Cuentas de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@veterinaria.cl | admin123 |
| Recepcionista | recepcion@veterinaria.cl | recep123 |
| Cliente | cliente@correo.cl | 1234 |

## Seguridad

- `POST /api/auth/login` entrega un **token de acceso JWT** (15 minutos) y un **token de renovación** (7 días).
- Las rutas privadas usan el encabezado `Authorization: Bearer <token>`. El gateway lo reenvía y cada microservicio valida el token y el rol.
- `POST /api/auth/refresh` entrega tokens nuevos y anula el anterior; `POST /api/auth/logout` cierra la sesión.
- Los tres microservicios comparten la clave del JWT (variable de entorno `JWT_SECRETO`).

En Swagger: ejecutar el login, copiar `tokenAcceso`, presionar **Authorize** y pegarlo.

## Pruebas unitarias

Desde VS Code (Testing) o, con Maven instalado:

```bash
mvn test
```
