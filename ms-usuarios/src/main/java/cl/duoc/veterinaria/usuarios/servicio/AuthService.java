package cl.duoc.veterinaria.usuarios.servicio;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.veterinaria.usuarios.dto.LoginRequest;
import cl.duoc.veterinaria.usuarios.dto.RegistroRequest;
import cl.duoc.veterinaria.usuarios.dto.SesionUsuario;
import cl.duoc.veterinaria.usuarios.dto.TokenResponse;
import cl.duoc.veterinaria.usuarios.excepcion.NoAutorizadoException;
import cl.duoc.veterinaria.usuarios.modelo.TokenRenovacion;
import cl.duoc.veterinaria.usuarios.modelo.Usuario;
import cl.duoc.veterinaria.usuarios.repositorio.TokenRenovacionRepository;
import cl.duoc.veterinaria.usuarios.repositorio.UsuarioRepository;
import cl.duoc.veterinaria.usuarios.seguridad.JwtService;

/**
 * Inicio de sesión, registro, renovación y cierre de sesión.
 *
 * Flujo de tokens:
 * 1. Login correcto: se entrega un token de acceso (JWT, 15 min) y un token de renovación (7 días).
 * 2. Cuando el token de acceso vence, el frontend envía el de renovación a /api/auth/refresh
 *    y recibe un par nuevo. El token de renovación usado queda anulado (rotación).
 * 3. Al cerrar sesión, el token de renovación se anula en la base de datos.
 */
@Service
public class AuthService {

    private static final String MENSAJE_LOGIN = "Correo o contraseña incorrectos, o usuario desactivado";

    private final UsuarioRepository usuarios;
    private final TokenRenovacionRepository tokens;
    private final UsuarioService usuarioService;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final long diasRenovacion;
    private final SecureRandom aleatorio = new SecureRandom();

    public AuthService(UsuarioRepository usuarios, TokenRenovacionRepository tokens, UsuarioService usuarioService,
                       PasswordEncoder encoder, JwtService jwtService,
                       @Value("${jwt.dias-renovacion}") long diasRenovacion) {
        this.usuarios = usuarios;
        this.tokens = tokens;
        this.usuarioService = usuarioService;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.diasRenovacion = diasRenovacion;
    }

    @Transactional
    public TokenResponse iniciarSesion(LoginRequest datos) {
        Usuario usuario = usuarios.findByCorreoIgnoreCase(datos.correo().trim())
                .filter(Usuario::isActivo)
                .filter(encontrado -> encoder.matches(datos.clave(), encontrado.getClave()))
                .orElseThrow(() -> new NoAutorizadoException(MENSAJE_LOGIN));
        return emitirTokens(usuario);
    }

    @Transactional
    public TokenResponse registrar(RegistroRequest datos) {
        Usuario nuevo = usuarioService.registrarCliente(datos);
        return emitirTokens(nuevo);
    }

    @Transactional
    public TokenResponse renovar(String tokenRenovacion) {
        TokenRenovacion guardado = tokens.findByToken(tokenRenovacion)
                .filter(TokenRenovacion::esUsable)
                .orElseThrow(() -> new NoAutorizadoException("La sesión expiró, inicia sesión de nuevo"));
        Usuario usuario = guardado.getUsuario();
        if (!usuario.isActivo()) {
            throw new NoAutorizadoException("La cuenta está desactivada");
        }
        guardado.setRevocado(true); // rotación: cada token de renovación se usa una sola vez
        tokens.save(guardado);
        return emitirTokens(usuario);
    }

    @Transactional
    public void cerrarSesion(String tokenRenovacion) {
        tokens.findByToken(tokenRenovacion).ifPresent(guardado -> {
            guardado.setRevocado(true);
            tokens.save(guardado);
        });
    }

    private TokenResponse emitirTokens(Usuario usuario) {
        String tokenAcceso = jwtService.generarTokenAcceso(usuario);
        TokenRenovacion renovacion = new TokenRenovacion(generarTextoAleatorio(), usuario,
                Instant.now().plus(diasRenovacion, ChronoUnit.DAYS));
        tokens.save(renovacion);
        return new TokenResponse(tokenAcceso, renovacion.getToken(), "Bearer", jwtService.segundosDeVida(),
                SesionUsuario.desde(usuario));
    }

    /** 32 bytes aleatorios en Base64 (43 caracteres): imposible de adivinar */
    private String generarTextoAleatorio() {
        byte[] bytes = new byte[32];
        aleatorio.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
