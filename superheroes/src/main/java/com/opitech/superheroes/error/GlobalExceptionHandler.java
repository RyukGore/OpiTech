package com.opitech.superheroes.error;

import com.opitech.superheroes.exception.HeroAlreadyExistsException;
import com.opitech.superheroes.exception.HeroNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ApiError buildError(HttpStatus status, String message, HttpServletRequest request) {
        return new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
    }

    // 404 - recurso no encontrado
    @ExceptionHandler(HeroNotFoundException.class)
    public ResponseEntity<ApiError> handleHeroNotFound(HeroNotFoundException ex,
                                                       HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiError error = buildError(status, ex.getMessage(), request);
        return ResponseEntity.status(status).body(error);
    }

    // 409 - conflicto por duplicidad
    @ExceptionHandler(HeroAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleHeroAlreadyExists(HeroAlreadyExistsException ex,
                                                            HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ApiError error = buildError(status, ex.getMessage(), request);
        return ResponseEntity.status(status).body(error);
    }

    // 400 - validaciones de body @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .orElse("Validation error");

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError error = buildError(status, message, request);
        return ResponseEntity.status(status).body(error);
    }

    // 400 - parámetros de request inválidos (por ejemplo, search sin name)
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError error = buildError(status, ex.getMessage(), request);
        return ResponseEntity.status(status).body(error);
    }

    // 500 - cualquier otra cosa inesperada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiError error = buildError(status, ex.getMessage(), request);
        return ResponseEntity.status(status).body(error);
    }
}