package cl.duoc.veterinaria.usuarios.modelo;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Token de renovación (refresh token): permite pedir un token de acceso nuevo
 * sin volver a escribir la contraseña. Se guarda en la base de datos para poder
 * anularlo al cerrar sesión, y cada uno se usa una sola vez (rotación).
 */
@Entity
@Table(name = "tokens_renovacion")
public class TokenRenovacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private Instant expiraEn;

    @Column(nullable = false)
    private boolean revocado = false;

    protected TokenRenovacion() {
    }

    public TokenRenovacion(String token, Usuario usuario, Instant expiraEn) {
        this.token = token;
        this.usuario = usuario;
        this.expiraEn = expiraEn;
    }

    /** Se puede usar si no fue anulado y todavía no vence */
    public boolean esUsable() {
        return !revocado && expiraEn.isAfter(Instant.now());
    }

    public Long getId() { return id; }
    public String getToken() { return token; }
    public Usuario getUsuario() { return usuario; }
    public Instant getExpiraEn() { return expiraEn; }
    public boolean isRevocado() { return revocado; }
    public void setRevocado(boolean revocado) { this.revocado = revocado; }
}
