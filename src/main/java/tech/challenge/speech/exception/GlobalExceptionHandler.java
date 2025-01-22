package tech.challenge.speech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tech.challenge.speech.model.dto.ApiResponseWrapper;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(objectError -> objectError instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        : objectError.getDefaultMessage())
                .collect(Collectors.toList());

        ApiResponseWrapper<Object> response = new ApiResponseWrapper<>(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                null,
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleResourceNotFoundException(NotFoundException ex) {
        ApiResponseWrapper<Void> response = new ApiResponseWrapper<>(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                null,
                null
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleGeneralException(Exception ex) {
        ApiResponseWrapper<Object> response = new ApiResponseWrapper<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                null,
                List.of(ex.getMessage())
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}