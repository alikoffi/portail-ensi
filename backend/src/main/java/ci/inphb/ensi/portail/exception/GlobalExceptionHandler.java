package ci.inphb.ensi.portail.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Transforme les exceptions en reponses JSON homogenes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PortailException.class)
    public ResponseEntity<Map<String, Object>> handlePortail(PortailException ex) {
        return ResponseEntity.status(ex.getStatus()).body(corps(ex.getStatus(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> corps = corps(HttpStatus.BAD_REQUEST, "Donnees invalides");
        Map<String, String> erreurs = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            erreurs.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        corps.put("erreurs", erreurs);
        return ResponseEntity.badRequest().body(corps);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleFichierTropVolumineux(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(corps(HttpStatus.PAYLOAD_TOO_LARGE, "Fichier trop volumineux (25 Mo maximum)"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccesRefuse(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(corps(HttpStatus.FORBIDDEN, "Accès refusé : action réservée à l'administrateur"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenerale(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(corps(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue"));
    }

    private Map<String, Object> corps(HttpStatus status, String message) {
        Map<String, Object> corps = new HashMap<>();
        corps.put("timestamp", LocalDateTime.now().toString());
        corps.put("status", status.value());
        corps.put("error", status.getReasonPhrase());
        corps.put("message", message);
        return corps;
    }
}
