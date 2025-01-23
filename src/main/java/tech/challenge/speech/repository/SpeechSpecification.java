package tech.challenge.speech.repository;

import jakarta.persistence.criteria.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tech.challenge.speech.model.entity.Speech;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class SpeechSpecification {

    private static final String WILDCARD = "%";

    public static Specification<Speech> checkForDuplicate(Speech speech) {

        return (root, query, builder) -> {
            Predicate[] predicates = Stream.of(
                            StringUtils.isNotBlank(speech.getAuthor())
                                    ? builder.equal(builder.lower(root.get("author")), speech.getAuthor().toLowerCase())
                                    : null,
                            StringUtils.isNotBlank(speech.getContent())
                                    ? builder.equal(builder.lower(root.get("content")), speech.getContent().toLowerCase())
                                    : null,
                            Objects.nonNull(speech.getSpeechDate())
                                    ? builder.equal(root.get("speechDate"), speech.getSpeechDate())
                                    : null,
                            CollectionUtils.isNotEmpty(speech.getKeywords())
                                    ? builder.equal(builder.size(root.get("keywords")), speech.getKeywords().size())
                                    : null,
                            CollectionUtils.isNotEmpty(speech.getKeywords())
                                    ? builder.and(
                                    CollectionUtils.emptyIfNull(speech.getKeywords()).stream()
                                            .map(keyword -> builder.isMember(keyword, root.get("keywords")))
                                            .toArray(Predicate[]::new))
                                    : null)
                    .filter(Objects::nonNull)
                    .toArray(Predicate[]::new);

            return builder.and(predicates);
        };
    }

    public static Specification<Speech> filterSpeeches(
            String author, String snippet, OffsetDateTime startDate, OffsetDateTime endDate, Set<String> keywords) {

        return (speech, query, builder) -> {
            Predicate[] predicates = Stream.of(
                            StringUtils.isNotBlank(author)
                                    ? builder.like(builder.lower(speech.get("author")), author.toLowerCase() + WILDCARD)
                                    : null,
                            StringUtils.isNotBlank(snippet)
                                    ? builder.like(builder.lower(speech.get("content")), WILDCARD + snippet.toLowerCase() + WILDCARD)
                                    : null,
                            CollectionUtils.isNotEmpty(keywords)
                                    ? builder.or(keywords.stream()
                                    .map(keyword -> builder.isMember(keyword, speech.get("keywords")))
                                    .toArray(Predicate[]::new))
                                    : null,
                            ObjectUtils.allNotNull(startDate, endDate)
                                    ? builder.between(speech.get("speechDate"), startDate, endDate)
                                    : null)
                    .filter(Objects::nonNull)
                    .toArray(Predicate[]::new);

            return builder.and(predicates);
        };
    }

}