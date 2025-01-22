package tech.challenge.speech.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class UpdateSpeechDTO {

    @NotNull
    private Long id;

    @NotBlank
    private String content;

    @NotBlank
    private String author;

    @NotNull
    private OffsetDateTime speechDate;

    @NotNull
    private Set<String> keywords;
}