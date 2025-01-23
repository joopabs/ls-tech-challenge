package tech.challenge.speech.common;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tech.challenge.speech.model.dto.ApiResponseWrapper;

import java.util.List;

@UtilityClass
public class ApiResponseBuilder {

    public <T> ResponseEntity<ApiResponseWrapper<T>> buildResponse(HttpStatus status, String message, T data, List<String> errors) {
        ApiResponseWrapper<T> response = new ApiResponseWrapper<>(status.value(), message, data, errors);
        return ResponseEntity.status(status).body(response);
    }

    public <T> ResponseEntity<ApiResponseWrapper<T>> buildResponse(HttpStatus status, String message, T data) {
        return buildResponse(status, message, data, null);
    }
}