package tech.challenge.speech.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tech.challenge.speech.entity.Speech;
import tech.challenge.speech.repository.SpeechRepository;
import tech.challenge.speech.service.SpeechService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Transactional
class SpeechServiceIntegrationTest {

    @Autowired
    private SpeechService speechService;

    @Autowired
    private SpeechRepository speechRepository;

    @BeforeEach
    void setup() {
        // Sample data
        Speech speech1 = new Speech();
        speech1.setContent("Equality and justice for all");
        speech1.setAuthor("John Doe");
        speech1.setSpeechDate(OffsetDateTime.parse("2023-01-01T10:00:00Z"));
        speech1.setKeywords(Set.of("equality", "justice"));

        Speech speech2 = new Speech();
        speech2.setContent("Human rights are non-negotiable");
        speech2.setAuthor("Jane Smith");
        speech2.setSpeechDate(OffsetDateTime.parse("2023-02-15T15:30:00Z"));
        speech2.setKeywords(Set.of("rights", "freedom"));

        Speech speech3 = new Speech();
        speech3.setContent("Economic stability is key to peace");
        speech3.setAuthor("John Doe");
        speech3.setSpeechDate(OffsetDateTime.parse("2023-03-10T12:00:00Z"));
        speech3.setKeywords(Set.of("economy", "peace"));

        // Save to database
        speechRepository.saveAll(List.of(speech1, speech2, speech3));
    }

    @Test
    void shouldSearchSpeechesByAuthor() {
        List<Speech> result = speechService.searchSpeeches("John Doe", null, null, null, null);

        // 2 speeches authored by John Doe
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(
                hasProperty("content", is("Equality and justice for all")),
                hasProperty("content", is("Economic stability is key to peace"))
        ));
    }

    @Test
    void shouldSearchSpeechesBySnippet() {
        List<Speech> result = speechService.searchSpeeches(null, "rights", null, null, null);

        // Only speech2 contains "rights"
        assertThat(result, hasSize(1));
        assertThat(result.getFirst(), hasProperty("author", is("Jane Smith")));
    }

    @Test
    void shouldSearchSpeechesByDateRange() {
        List<Speech> result = speechService.searchSpeeches(
                null, null,
                OffsetDateTime.parse("2023-01-01T00:00:00Z"),
                OffsetDateTime.parse("2023-02-28T23:59:59Z"),
                null
        );

        // speech1 and speech2 are within this date range
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(
                hasProperty("author", is("John Doe")),
                hasProperty("author", is("Jane Smith"))
        ));
    }

    @Test
    void shouldSearchSpeechesByKeywords() {
        List<Speech> result = speechService.searchSpeeches(
                null, null, null, null, Set.of("economy", "freedom")
        );

        // speech2 and speech3 match these keywords
        assertThat(result, hasSize(2));
        assertThat(result, containsInAnyOrder(
                hasProperty("content", is("Human rights are non-negotiable")),
                hasProperty("content", is("Economic stability is key to peace"))
        ));
    }

    @Test
    void shouldPerformCombinedSearchByAuthorAndKeywords() {
        List<Speech> result = speechService.searchSpeeches(
                "John Doe", null, null, null, Set.of("economy")
        );

        // Matches speech3
        assertThat(result, hasSize(1));
        assertThat(result.getFirst(), hasProperty("content", is("Economic stability is key to peace")));
    }

    @Test
    void shouldSearchSpeechesByAllParameters() {
        List<Speech> result = speechService.searchSpeeches(
                "John Doe",
                "justice",
                OffsetDateTime.parse("2023-01-01T00:00:00Z"),
                OffsetDateTime.parse("2023-01-02T23:59:59Z"),
                Set.of("equality", "justice")
        );

        // Matches speech1 as it meets all the criteria
        assertThat(result, hasSize(1));
        assertThat(result.getFirst(), allOf(
                hasProperty("author", is("John Doe")),
                hasProperty("content", is("Equality and justice for all")),
                hasProperty("speechDate", is(OffsetDateTime.parse("2023-01-01T10:00:00Z"))),
                hasProperty("keywords", containsInAnyOrder("equality", "justice"))
        ));
    }

    @Test
    void shouldReturnEmptyWhenNoMatchesFound() {
        List<Speech> result = speechService.searchSpeeches(
                "Nonexistent Author",
                "nonexistent snippet",
                OffsetDateTime.parse("2024-01-01T00:00:00Z"),
                OffsetDateTime.parse("2024-01-31T23:59:59Z"),
                Set.of("nonexistent", "keyword")
        );

        // No speech should match these criteria
        assertThat(result, is(empty()));
    }
}