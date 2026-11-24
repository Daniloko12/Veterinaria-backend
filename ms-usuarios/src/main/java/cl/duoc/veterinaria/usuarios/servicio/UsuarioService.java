package cl.duoc.veterinaria.usuarios.servicio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.veterinaria.usuarios.dto.RegistroRequest;
import cl.duoc.veterinaria.usuarios.dto.UsuarioRequest;
import cl.duoc.veterinaria.usuarios.dto.UsuarioResponse;
import cl.duoc.veterinaria.usuarios.excepcion.ConflictoException;
import cl.duoc.veterinaria.usuarios.excepcion.RecursoNoEncontradoException;
import cl.duoc.veterinaria.usuarios.excepcion.SolicitudInvalidaException;
import cl.duoc.veterinaria.usuarios.modelo.Rol;
import cl.duoc.veterinaria.usuarios.modelo.Usuario;
import cl.duoc.veterinaria.usuarios.repositorio.TokenRenovacionRepository;
import cl.duoc.veterinaria.usuarios.repositorio.UsuarioRepository;

/**
 * Lógica de negocio de los usuarios:
 * RUN válido (módulo 11), RUN y correo únicos, contraseña cifrada con BCrypt
 * y un administrador no puede eliminarse ni desactivarse a sí mismo.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarios;
    private final TokenRenovacionRepository tokens;
    private final PasswordEncoder encoder;

    public UsuarioService(UsuarioRepository usuarios, TokenRenovacionRepository tokens, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarios.findAll(Sort.by("id")).stream().map(UsuarioResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.desde(buscar(id));
    }

    /** Registro desde la tienda: siempre crea un cliente */
    @Transactional
    public Usuario registrarCliente(RegistroRequest datos) {
        validarRunYCorreo(datos.run(), datos.correo(), null);
        Usuario usuario = new Usuario();
        usuario.setRun(normalizarRun(datos.run()));
        usuario.setNombre(datos.nombre().trim());
        usuario.setApellidos(datos.apellidos().trim());
        usuario.setCorreo(datos.correo().trim().toLowerCase());
        usuario.setClave(encoder.encode(datos.clave()));
        usuario.setFechaNacimiento(convertirFecha(datos.fechaNacimiento()));
        usuario.setRol(Rol.CLIENTE);
        usuario.setRegion(datos.region());
        usuario.setComuna(datos.comuna());
        usuario.setDireccion(datos.direccion().trim());
        usuario.setActivo(true);
        return usuarios.save(usuario);
    }

    /** Creación desde el panel de administración (puede elegir el rol) */
    @Transactional
    public UsuarioResponse crear(UsuarioRequest datos) {
        validarRunYCorreo(datos.run(), datos.correo(), null);
        String clave = textoOpcional(datos.clave());
        if (clave == null) {
            throw new SolicitudInvalidaException("clave", "La contraseña es obligatoria");
        }
        Usuario usuario = new Usuario();
        copiarDatos(datos, usuario);
        usuario.setClave(encoder.encode(validarLargoClave(clave)));
        usuario.setActivo(datos.activo() == null || datos.activo());
        return UsuarioResponse.desde(usuarios.save(usuario));
    }

    /** Si la contraseña viene vacía se mantiene la actual */
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest datos, Long idActual) {
        Usuario usuario = buscar(id);
        validarRunYCorreo(datos.run(), datos.correo(), id);
        if (id.equals(idActual) && Boolean.FALSE.equals(datos.activo())) { // idActual null = edición del propio perfil
            throw new ConflictoException("activo", "No puedes desactivar tu propia cuenta");
        }
        if (id.equals(idActual) && Rol.desdeTexto(datos.rol()) != usuario.getRol()) {
            throw new ConflictoException("rol", "No puedes cambiar tu propio rol");
        }
        copiarDatos(datos, usuario);
        String clave = textoOpcional(datos.clave());
        if (clave != null) {
            usuario.setClave(encoder.encode(validarLargoClave(clave)));
        }
        if (datos.activo() != null) {
            usuario.setActivo(datos.activo());
        }
        return UsuarioResponse.desde(usuarios.save(usuario));
    }

    /**
     * El usuario conectado edita sus propios datos (vista "Mi perfil").
     * El rol y el estado no se pueden cambiar desde aquí.
     */
    @Transactional
    public UsuarioResponse actualizarPerfil(Long id, UsuarioRequest datos) {
        Usuario usuario = buscar(id);
        UsuarioRequest sinCambiarRol = new UsuarioRequest(datos.run(), datos.nombre(), datos.apellidos(), datos.correo(),
                datos.clave(), datos.fechaNacimiento(), usuario.getRol().aTexto(), datos.region(), datos.comuna(),
                datos.direccion(), usuario.isActivo());
        return actualizar(id, sinCambiarRol, null);
    }

    @Transactional
    public UsuarioResponse cambiarEstado(Long id, boolean activo, Long idActual) {
        if (id.equals(idActual) && !activo) {
            throw new ConflictoException("No puedes desactivar tu propia cuenta");
        }
        Usuario usuario = buscar(id);
        usuario.setActivo(activo);
        return UsuarioResponse.desde(usuarios.save(usuario));
    }

    @Transactional
    public void eliminar(Long id, Long idActual) {
        if (id.equals(idActual)) {
            throw new ConflictoException("No puedes eliminar tu propia cuenta");
        }
        Usuario usuario = buscar(id);
        tokens.deleteByUsuario(usuario);
        usuarios.delete(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscar(Long id) {
        return usuarios.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario con id " + id));
    }

    // ---------- reglas de negocio ----------

    private void validarRunYCorreo(String run, String correo, Long idExcluir) {
        if (!RunValidador.esValido(run)) {
            throw new SolicitudInvalidaException("run", "El RUN no es válido");
        }
        String runNormalizado = normalizarRun(run);
        String correoNormalizado = correo.trim();
        boolean runRepetido = idExcluir == null
                ? usuarios.existsByRunIgnoreCase(runNormalizado)
                : usuarios.existsByRunIgnoreCaseAndIdNot(runNormalizado, idExcluir);
        if (runRepetido) {
            throw new ConflictoException("run", "El RUN ya está registrado");
        }
        boolean correoRepetido = idExcluir == null
                ? usuarios.existsByCorreoIgnoreCase(correoNormalizado)
                : usuarios.existsByCorreoIgnoreCaseAndIdNot(correoNormalizado, idExcluir);
        if (correoRepetido) {
            throw new ConflictoException("correo", "El correo ya está registrado");
        }
    }

    private void copiarDatos(UsuarioRequest datos, Usuario usuario) {
        Rol rol = Rol.desdeTexto(datos.rol());
        if (rol == null) {
            throw new SolicitudInvalidaException("rol", "El rol no existe");
        }
        usuario.setRun(normalizarRun(datos.run()));
        usuario.setNombre(datos.nombre().trim());
        usuario.setApellidos(datos.apellidos().trim());
        usuario.setCorreo(datos.correo().trim().toLowerCase());
        usuario.setFechaNacimiento(convertirFecha(datos.fechaNacimiento()));
        usuario.setRol(rol);
        usuario.setRegion(datos.region());
        usuario.setComuna(datos.comuna());
        usuario.setDireccion(datos.direccion().trim());
    }

    private static String validarLargoClave(String clave) {
        if (clave.length() < 4 || clave.length() > 10) {
            throw new SolicitudInvalidaException("clave", "La contraseña debe tener entre 4 y 10 caracteres");
        }
        return clave;
    }

    private static String normalizarRun(String run) {
        return run.trim().toUpperCase();
    }

    private static String textoOpcional(String texto) {
        return texto == null || texto.isBlank() ? null : texto;
    }

    private static LocalDate convertirFecha(String texto) {
        return texto == null || texto.isBlank() ? null : LocalDate.parse(texto);
    }
}
