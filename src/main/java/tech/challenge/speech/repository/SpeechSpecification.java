package tech.challenge.speech.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import tech.challenge.speech.model.entity.Speech;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class SpeechSpecification {

    private static final String WILDCARD = "%";

    public static Specification<Speech> filterSpeeches(String author, String snippet, OffsetDateTime startDate, OffsetDateTime endDate, Set<String> keywords) {
        return (speech, query, builder) -> {
            List<Predicate> predicates = Stream.of(
                            buildAuthorPredicate(author, speech, builder),
                            buildSnippetPredicate(snippet, speech, builder),
                            buildKeywordsPredicate(keywords, speech, builder),
                            buildDateRangePredicate(startDate, endDate, speech, builder)
                    )
                    .filter(Objects::nonNull) // Ignore null predicates
                    .toList();

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate buildAuthorPredicate(String author, Root<Speech> speech, CriteriaBuilder builder) {
        return author != null
                ? builder.equal(builder.upper(speech.get("author")), author.toUpperCase())
                : null;
    }

    private static Predicate buildSnippetPredicate(String snippet, Root<Speech> speech, CriteriaBuilder builder) {
        return snippet != null
                ? builder.like(builder.upper(speech.get("content")), WILDCARD + snippet.toUpperCase() + WILDCARD)
                : null;
    }

    private static Predicate buildKeywordsPredicate(Set<String> keywords, Root<Speech> speech, CriteriaBuilder builder) {
        if (keywords == null || keywords.isEmpty()) {
            return null;
        }
        return builder.or(
                keywords.stream()
                        .map(keyword -> builder.isMember(keyword, speech.get("keywords")))
                        .toArray(Predicate[]::new)
        );
    }

    private static Predicate buildDateRangePredicate(OffsetDateTime startDate, OffsetDateTime endDate, Root<Speech> speech, CriteriaBuilder builder) {
        return (startDate != null && endDate != null)
                ? builder.between(speech.get("speechDate"), startDate, endDate)
                : null;
    }
}