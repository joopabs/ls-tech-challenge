package tech.challenge.speech.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tech.challenge.speech.common.ApiResponseBuilder;
import tech.challenge.speech.model.dto.ApiResponseWrapper;

import java.util.List;
import java.util.stream.Collectors;

import static tech.challenge.speech.common.ApiResponseBuilder.build;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(objectError -> objectError instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        : objectError.getDefaultMessage())
                .collect(Collectors.toList());

        return build(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                null,
                errors
        );
    }

    @ExceptionHandler(DuplicateSpeechException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleDuplicateSpeechException(DuplicateSpeechException ex) {
        return ApiResponseBuilder.build(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponseWrapper<Void>> handleResourceNotFoundException(NotFoundException ex) {
        return ApiResponseBuilder.build(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseWrapper<Object>> handleGeneralException(Exception ex) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                null,
                List.of(ex.getMessage())
        );
    }
}