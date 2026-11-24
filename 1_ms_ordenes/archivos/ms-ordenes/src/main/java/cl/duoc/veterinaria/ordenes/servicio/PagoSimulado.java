package cl.duoc.veterinaria.ordenes.servicio;

import java.security.SecureRandom;
import java.time.YearMonth;
import java.util.Set;

import org.springframework.stereotype.Component;

/**
 * "Banco" simulado. No cobra dinero real:
 * - La tarjeta 4000 0000 0000 0002 siempre se rechaza (sirve para mostrar el pago con error).
 * - Una tarjeta con fecha de vencimiento pasada se rechaza.
 * - Cualquier otra tarjeta válida se aprueba con un código de autorización de 6 dígitos.
 */
@Component
public class PagoSimulado {

    private static final Set<String> TARJETAS_RECHAZADAS = Set.of("4000000000000002");

    private final SecureRandom aleatorio = new SecureRandom();

    public static String soloDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }

    public boolean esRechazada(String numero) {
        return TARJETAS_RECHAZADAS.contains(soloDigitos(numero));
    }

    /** "MM/AA": el mes debe estar entre 01 y 12 */
    public boolean mesValido(String vencimiento) {
        int mes = Integer.parseInt(vencimiento.substring(0, 2));
        return mes >= 1 && mes <= 12;
    }

    /** La tarjeta sirve hasta el último día del mes de vencimiento */
    public boolean estaVencida(String vencimiento) {
        int mes = Integer.parseInt(vencimiento.substring(0, 2));
        int anio = 2000 + Integer.parseInt(vencimiento.substring(3, 5));
        return YearMonth.of(anio, mes).isBefore(YearMonth.now());
    }

    public String detectarMarca(String numero) {
        String digitos = soloDigitos(numero);
        if (digitos.startsWith("4")) {
            return "Visa";
        }
        if (digitos.matches("^(5[1-5]|2[2-7]).*")) {
            return "Mastercard";
        }
        if (digitos.matches("^3[47].*")) {
            return "American Express";
        }
        return "Tarjeta";
    }

    public String ultimosCuatro(String numero) {
        String digitos = soloDigitos(numero);
        return digitos.length() <= 4 ? digitos : digitos.substring(digitos.length() - 4);
    }

    public String generarCodigoAutorizacion() {
        return String.valueOf(100000 + aleatorio.nextInt(900000));
    }
}
