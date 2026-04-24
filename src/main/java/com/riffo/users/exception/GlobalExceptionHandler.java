package com.riffo.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire global des exceptions.
 *
 * Correct : centralise la gestion des erreurs HTTP.
 * avant : le Controller retournait ResponseEntity.badRequest().build() sans
 * corps,
 * laissant le client sans information sur l'erreur.
 * après : réponses JSON structurées avec timestamp, status et message.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les partenaires introuvables -> HTTP 404.
     */
    @ExceptionHandler(PartenaireNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(PartenaireNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Gère les erreurs de validation (@Valid) -> HTTP 400 avec détail par champ.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String field = ((FieldError) err).getField();
            fieldErrors.put(field, err.getDefaultMessage());
        });
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 400);
        body.put("erreur", "Données de saisie invalides");
        body.put("details", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Gère les arguments illégaux (ex: email déjà existant) -> HTTP 409 Conflict.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildError(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Capture toute autre exception non prévue -> HTTP 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue");
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("erreur", message);
        return ResponseEntity.status(status).body(body);
    }
}
