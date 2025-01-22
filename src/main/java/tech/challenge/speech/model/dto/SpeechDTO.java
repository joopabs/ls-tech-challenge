package tech.challenge.speech.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class SpeechDTO {

    private Long id;

    @NotEmpty(message = "Content cannot be empty")
    private String content;

    @NotEmpty(message = "Author cannot be empty")
    private String author;

    @NotNull(message = "Keywords cannot be null")
    @NotEmpty(message = "Keywords cannot be empty")
    private Set<String> keywords;

    @NotNull(message = "Speech date cannot be null")
    private OffsetDateTime speechDate;
}
