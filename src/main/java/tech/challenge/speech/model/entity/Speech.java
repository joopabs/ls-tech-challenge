package tech.challenge.speech.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "speech")
public class Speech {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Speech speech = (Speech) o;
        return Objects.equals(id, speech.id) &&
                Objects.equals(content, speech.content) &&
                Objects.equals(author, speech.author) &&
                Objects.equals(keywords, speech.keywords) &&
                Objects.equals(speechDate, speech.speechDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content, author, keywords, speechDate);
    }
}