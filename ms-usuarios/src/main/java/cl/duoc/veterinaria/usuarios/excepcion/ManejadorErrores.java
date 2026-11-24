package cl.duoc.veterinaria.usuarios.excepcion;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Convierte las excepciones en respuestas JSON con el código HTTP correcto,
 * para que el frontend pueda mostrar el mensaje al usuario.
 */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> datosInvalidos(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return responder(HttpStatus.BAD_REQUEST, "Hay datos inválidos en la solicitud", errores);
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ErrorRespuesta> solicitudInvalida(SolicitudInvalidaException ex) {
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getErrores());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> cuerpoInvalido(HttpMessageNotReadableException ex) {
        return responder(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no tiene un formato válido", Map.of());
    }

    @ExceptionHandler(NoAutorizadoException.class)
    public ResponseEntity<ErrorRespuesta> noAutorizado(NoAutorizadoException ex) {
        return responder(HttpStatus.UNAUTHORIZED, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> noEncontrado(RecursoNoEncontradoException ex) {
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
    }

    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ErrorRespuesta> conflicto(ConflictoException ex) {
        return responder(HttpStatus.CONFLICT, ex.getMessage(), ex.getErrores());
    }

    private ResponseEntity<ErrorRespuesta> responder(HttpStatus estado, String mensaje, Map<String, String> errores) {
        return ResponseEntity.status(estado).body(new ErrorRespuesta(estado.value(), mensaje, errores));
    }
}
