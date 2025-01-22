package tech.challenge.speech.model.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class SpeechDTO {
    private Long id;
    private String content;
    private String author;
    private Set<String> keywords;
    private OffsetDateTime speechDate;
}
