package cl.duoc.veterinaria.usuarios.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import cl.duoc.veterinaria.usuarios.modelo.Rol;
import cl.duoc.veterinaria.usuarios.modelo.Usuario;
import cl.duoc.veterinaria.usuarios.repositorio.UsuarioRepository;

/** Si la tabla está vacía, crea las 3 cuentas de prueba (una por rol) */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;

    public DatosIniciales(UsuarioRepository usuarios, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (usuarios.count() > 0) {
            return;
        }
        usuarios.save(crear("123456785", "Administrador", "Demo", "admin@veterinaria.cl", "admin123",
                Rol.ADMINISTRADOR, "Av. Brasil 123"));
        usuarios.save(crear("222222222", "Recepción", "Demo", "recepcion@veterinaria.cl", "recep123",
                Rol.RECEPCIONISTA, "Av. Brasil 123"));
        usuarios.save(crear("111111111", "Cliente", "Demo", "cliente@correo.cl", "1234",
                Rol.CLIENTE, "Calle Los Aromos 456"));
    }

    private Usuario crear(String run, String nombre, String apellidos, String correo, String clave, Rol rol,
                          String direccion) {
        Usuario usuario = new Usuario();
        usuario.setRun(run);
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setCorreo(correo);
        usuario.setClave(encoder.encode(clave));
        usuario.setRol(rol);
        usuario.setRegion("O'Higgins");
        usuario.setComuna("Rancagua");
        usuario.setDireccion(direccion);
        usuario.setActivo(true);
        return usuario;
    }
}
