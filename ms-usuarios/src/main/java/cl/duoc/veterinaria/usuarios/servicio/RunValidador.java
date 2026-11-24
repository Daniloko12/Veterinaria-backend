package cl.duoc.veterinaria.usuarios.servicio;

/** Valida un RUN chileno con el algoritmo del módulo 11 (igual que el frontend) */
public final class RunValidador {

    private RunValidador() {
    }

    /** Recibe el RUN sin puntos ni guion, por ejemplo "123456785" o "12345678K" */
    public static boolean esValido(String run) {
        if (run == null) {
            return false;
        }
        String limpio = run.trim().toUpperCase();
        if (!limpio.matches("^\\d{7,8}[0-9K]$")) {
            return false;
        }
        String cuerpo = limpio.substring(0, limpio.length() - 1);
        char dvIngresado = limpio.charAt(limpio.length() - 1);

        int suma = 0;
        int multiplicador = 2;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplicador;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }
        int resultado = 11 - (suma % 11);
        char dvCalculado = resultado == 11 ? '0' : resultado == 10 ? 'K' : Character.forDigit(resultado, 10);
        return dvCalculado == dvIngresado;
    }
}
