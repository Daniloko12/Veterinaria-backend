package cl.duoc.veterinaria.ordenes.seguridad;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.duoc.veterinaria.ordenes.excepcion.ErrorRespuesta;
import jakarta.servlet.http.HttpServletResponse;

/** Respuestas JSON para 401 (sin sesión o token inválido) y 403 (rol sin permiso) */
public final class RespuestasSeguridad {

    private RespuestasSeguridad() {
    }

    public static AuthenticationEntryPoint sinAutenticacion(ObjectMapper mapper) {
        return (request, response, ex) -> {
            Object error = request.getAttribute(JwtAuthFilter.ATRIBUTO_ERROR_TOKEN);
            String mensaje = error != null ? error.toString() : "Debes iniciar sesión para usar este recurso";
            escribir(response, mapper, HttpServletResponse.SC_UNAUTHORIZED, mensaje);
        };
    }

    public static AccessDeniedHandler sinPermiso(ObjectMapper mapper) {
        return (request, response, ex) -> escribir(response, mapper, HttpServletResponse.SC_FORBIDDEN,
                "Tu rol no tiene permiso para usar este recurso");
    }

    private static void escribir(HttpServletResponse response, ObjectMapper mapper, int estado, String mensaje)
            throws IOException {
        response.setStatus(estado);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(), ErrorRespuesta.de(estado, mensaje));
    }
}
