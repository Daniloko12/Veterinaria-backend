package cl.duoc.veterinaria.ordenes.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Lo que se guarda del pago. Por seguridad NUNCA se guarda el número completo
 * de la tarjeta ni el CVV: solo la marca y los últimos 4 dígitos.
 */
@Embeddable
public class PagoTarjeta {

    @Column(name = "pago_marca", length = 30)
    private String marca;

    @Column(name = "pago_ultimos4", length = 4)
    private String ultimos4;

    @Column(name = "pago_titular", length = 60)
    private String titular;

    @Column(name = "pago_codigo_autorizacion", length = 6)
    private String codigoAutorizacion;

    protected PagoTarjeta() {
    }

    public PagoTarjeta(String marca, String ultimos4, String titular, String codigoAutorizacion) {
        this.marca = marca;
        this.ultimos4 = ultimos4;
        this.titular = titular;
        this.codigoAutorizacion = codigoAutorizacion;
    }

    public String getMarca() { return marca; }
    public String getUltimos4() { return ultimos4; }
    public String getTitular() { return titular; }
    public String getCodigoAutorizacion() { return codigoAutorizacion; }
}
