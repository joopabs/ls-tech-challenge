package tech.challenge.speech.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.challenge.speech.model.entity.Speech;
import tech.challenge.speech.repository.SpeechRepository;
import tech.challenge.speech.repository.SpeechSpecification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class SpeechService {

    private final SpeechRepository speechRepository;

    public SpeechService(SpeechRepository speechRepository) {
        this.speechRepository = speechRepository;
    }

    public List<Speech> searchSpeeches(String author, String snippet, OffsetDateTime startDate, OffsetDateTime endDate, Set<String> keywords) {
        return speechRepository.findAll(
                SpeechSpecification.filterSpeeches(author, snippet, startDate, endDate, keywords)
        );
    }
}