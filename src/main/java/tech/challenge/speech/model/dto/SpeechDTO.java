package tech.challenge.speech.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime speechDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime createDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime updateDateTime;

    public void setKeywords(Set<String> keywords) {
        if (keywords != null) {
            this.keywords = keywords.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } else {
            this.keywords = null;
        }
    }
}
