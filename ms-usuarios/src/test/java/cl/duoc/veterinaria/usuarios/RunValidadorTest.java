package cl.duoc.veterinaria.usuarios;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import cl.duoc.veterinaria.usuarios.servicio.RunValidador;

/** Pruebas unitarias de la validación del RUN (módulo 11) */
class RunValidadorTest {

    @Test
    void aceptaRunesConDigitoVerificadorCorrecto() {
        assertTrue(RunValidador.esValido("123456785"));
        assertTrue(RunValidador.esValido("121231336"));
        assertTrue(RunValidador.esValido("10000013K"));
        assertTrue(RunValidador.esValido("10000013k"));
    }

    @Test
    void rechazaDigitoVerificadorIncorrecto() {
        assertFalse(RunValidador.esValido("121231334"));
        assertFalse(RunValidador.esValido("123456789"));
    }

    @Test
    void rechazaFormatoConPuntosOGuion() {
        assertFalse(RunValidador.esValido("12.345.678-5"));
        assertFalse(RunValidador.esValido("12345678-5"));
        assertFalse(RunValidador.esValido(""));
        assertFalse(RunValidador.esValido(null));
    }
}
