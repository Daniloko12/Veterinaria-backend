package cl.duoc.veterinaria.usuarios.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.veterinaria.usuarios.dto.EstadoRequest;
import cl.duoc.veterinaria.usuarios.dto.UsuarioRequest;
import cl.duoc.veterinaria.usuarios.dto.UsuarioResponse;
import cl.duoc.veterinaria.usuarios.seguridad.UsuarioAutenticado;
import cl.duoc.veterinaria.usuarios.servicio.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/** CRUD de usuarios. Todos los endpoints requieren rol ADMINISTRADOR (ver SecurityConfig). */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Mantenedor de usuarios (solo administrador)")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios")
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un usuario")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return usuarioService.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear usuario", description = "201 si se creó, 400 si hay datos inválidos, 409 si el RUN o el correo ya existen")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(datos));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Si la contraseña viene vacía se mantiene la actual")
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest datos,
                                      @AuthenticationPrincipal UsuarioAutenticado actual) {
        return usuarioService.actualizar(id, datos, actual.id());
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Activar o desactivar usuario")
    public UsuarioResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest datos,
                                         @AuthenticationPrincipal UsuarioAutenticado actual) {
        return usuarioService.cambiarEstado(id, datos.activo(), actual.id());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "204 si se eliminó. No permite eliminar la propia cuenta.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado actual) {
        usuarioService.eliminar(id, actual.id());
        return ResponseEntity.noContent().build();
    }
}
