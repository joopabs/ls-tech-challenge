package tech.challenge.speech.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createDateTime;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updateDateTime;

    public OffsetDateTime getCreateDateTime() {
        return createDateTime != null ? createDateTime.withOffsetSameInstant(ZoneOffset.UTC) : null;
    }

    public OffsetDateTime getUpdateDateTime() {
        return updateDateTime != null ? updateDateTime.withOffsetSameInstant(ZoneOffset.UTC) : null;
    }
}