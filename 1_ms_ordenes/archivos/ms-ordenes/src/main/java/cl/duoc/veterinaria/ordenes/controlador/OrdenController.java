package cl.duoc.veterinaria.ordenes.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.veterinaria.ordenes.dto.EstadoOrdenRequest;
import cl.duoc.veterinaria.ordenes.dto.OrdenRequest;
import cl.duoc.veterinaria.ordenes.dto.OrdenResponse;
import cl.duoc.veterinaria.ordenes.seguridad.UsuarioAutenticado;
import cl.duoc.veterinaria.ordenes.servicio.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ordenes")
@Tag(name = "Órdenes", description = "Compras con pago simulado con tarjeta y boletas")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @PostMapping
    @Operation(summary = "Comprar (checkout)",
            description = "Abierto a invitados. Si viene un token, la orden queda asociada a ese usuario. "
                    + "Responde 201 con la orden pagada o rechazada (ver estado y motivoRechazo).")
    public ResponseEntity<OrdenResponse> crear(@Valid @RequestBody OrdenRequest datos,
                                               @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crear(datos, usuario));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar todas las órdenes", description = "Administrador y recepcionista")
    public List<OrdenResponse> listar() {
        return ordenService.listar();
    }

    @GetMapping("/mis-ordenes")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Compras del usuario de la sesión")
    public List<OrdenResponse> misOrdenes(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ordenService.delCliente(usuario);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener una orden", description = "403 si un cliente pide la orden de otra persona")
    public OrdenResponse obtener(@PathVariable Long id, @AuthenticationPrincipal UsuarioAutenticado usuario) {
        return ordenService.obtener(id, usuario);
    }

    @PutMapping("/{id}/estado")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cambiar el estado (enviada, entregada)", description = "Administrador y recepcionista")
    public OrdenResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoOrdenRequest datos) {
        return ordenService.cambiarEstado(id, datos.estado());
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar una orden", description = "Solo administrador")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ordenService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
