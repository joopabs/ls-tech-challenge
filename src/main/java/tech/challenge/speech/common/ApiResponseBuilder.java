package tech.challenge.speech.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tech.challenge.speech.model.dto.ApiResponseWrapper;

import java.util.List;

public final class ApiResponseBuilder {

    public static <T> ResponseEntity<ApiResponseWrapper<T>> buildResponse(HttpStatus status, String message, T data, List<String> errors) {
        ApiResponseWrapper<T> response = new ApiResponseWrapper<>(status.value(), message, data, errors);
        return ResponseEntity.status(status).body(response);
    }

    public static <T> ResponseEntity<ApiResponseWrapper<T>> buildResponse(HttpStatus status, String message, T data) {
        return buildResponse(status, message, data, null);
    }
}