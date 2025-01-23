package tech.challenge.speech.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "speech")
public class Speech extends BaseEntity {

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String author;

    @ElementCollection
    @CollectionTable(name = "speech_keyword", joinColumns = @JoinColumn(name = "speech_id"))
    @Column(name = "keyword")
    private Set<String> keywords = Collections.emptySet();

    @Column(name = "speech_date", nullable = false)
    private OffsetDateTime speechDate;

    public OffsetDateTime getSpeechDate() {
        return speechDate != null ? speechDate.withOffsetSameInstant(ZoneOffset.UTC) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Speech speech = (Speech) o;
        return Objects.equals(content, speech.content) &&
                Objects.equals(author, speech.author) &&
                Objects.equals(keywords, speech.keywords) &&
                Objects.equals(speechDate, speech.speechDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, author, keywords, speechDate);
    }
}