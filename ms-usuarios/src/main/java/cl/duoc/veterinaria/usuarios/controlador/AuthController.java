package cl.duoc.veterinaria.usuarios.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.veterinaria.usuarios.dto.LoginRequest;
import cl.duoc.veterinaria.usuarios.dto.RegistroRequest;
import cl.duoc.veterinaria.usuarios.dto.RenovarRequest;
import cl.duoc.veterinaria.usuarios.dto.TokenResponse;
import cl.duoc.veterinaria.usuarios.dto.UsuarioRequest;
import cl.duoc.veterinaria.usuarios.dto.UsuarioResponse;
import cl.duoc.veterinaria.usuarios.seguridad.UsuarioAutenticado;
import cl.duoc.veterinaria.usuarios.servicio.AuthService;
import cl.duoc.veterinaria.usuarios.servicio.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Inicio de sesión, registro y tokens JWT")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Devuelve el token de acceso (JWT) y el token de renovación. 401 si los datos no coinciden.")
    public TokenResponse iniciarSesion(@Valid @RequestBody LoginRequest datos) {
        return authService.iniciarSesion(datos);
    }

    @PostMapping("/registro")
    @Operation(summary = "Crear cuenta de cliente", description = "Valida el RUN y que el RUN y el correo no estén registrados. Deja la sesión iniciada.")
    public ResponseEntity<TokenResponse> registrar(@Valid @RequestBody RegistroRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(datos));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar el token de acceso", description = "Recibe el token de renovación y entrega un par de tokens nuevo. El token usado queda anulado.")
    public TokenResponse renovar(@Valid @RequestBody RenovarRequest datos) {
        return authService.renovar(datos.tokenRenovacion());
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Anula el token de renovación en la base de datos.")
    public ResponseEntity<Void> cerrarSesion(@Valid @RequestBody RenovarRequest datos) {
        authService.cerrarSesion(datos.tokenRenovacion());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Datos del usuario de la sesión", description = "Sirve para comprobar que el token sigue siendo válido.")
    public UsuarioResponse miPerfil(@AuthenticationPrincipal UsuarioAutenticado actual) {
        return usuarioService.obtener(actual.id());
    }

    @PutMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar mi perfil", description = "Cualquier usuario conectado edita sus datos. El rol y el estado se mantienen. Contraseña vacía = mantener la actual.")
    public UsuarioResponse actualizarMiPerfil(@AuthenticationPrincipal UsuarioAutenticado actual,
                                              @Valid @RequestBody UsuarioRequest datos) {
        return usuarioService.actualizarPerfil(actual.id(), datos);
    }
}
