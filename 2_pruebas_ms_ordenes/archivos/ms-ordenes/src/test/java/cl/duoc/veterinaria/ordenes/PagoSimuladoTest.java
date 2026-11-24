package cl.duoc.veterinaria.ordenes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import cl.duoc.veterinaria.ordenes.servicio.PagoSimulado;

/** Pruebas unitarias del banco simulado */
class PagoSimuladoTest {

    private final PagoSimulado pago = new PagoSimulado();

    @Test
    void laTarjetaDePruebaDeRechazoSiempreSeRechaza() {
        assertTrue(pago.esRechazada("4000 0000 0000 0002"));
        assertFalse(pago.esRechazada("4242 4242 4242 4242"));
    }

    @Test
    void reconoceLaMarcaYLosUltimosCuatroDigitos() {
        assertEquals("Visa", pago.detectarMarca("4242 4242 4242 4242"));
        assertEquals("Mastercard", pago.detectarMarca("5555 5555 5555 4444"));
        assertEquals("4444", pago.ultimosCuatro("5555 5555 5555 4444"));
    }

    @Test
    void revisaElVencimiento() {
        assertTrue(pago.estaVencida("01/20"));
        assertFalse(pago.estaVencida("12/99"));
        assertFalse(pago.mesValido("13/30"));
        assertTrue(pago.mesValido("12/30"));
    }

    @Test
    void elCodigoDeAutorizacionTieneSeisDigitos() {
        assertTrue(pago.generarCodigoAutorizacion().matches("\\d{6}"));
    }
}
