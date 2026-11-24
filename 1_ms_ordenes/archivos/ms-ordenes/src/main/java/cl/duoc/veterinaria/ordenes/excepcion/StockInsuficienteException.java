package cl.duoc.veterinaria.ordenes.excepcion;

/** ms-productos respondió 409: la compra queda rechazada con este motivo */
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
