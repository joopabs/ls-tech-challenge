package tech.challenge.speech.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.challenge.speech.exception.NotFoundException;
import tech.challenge.speech.mapper.SpeechMapper;
import tech.challenge.speech.model.dto.SpeechDTO;
import tech.challenge.speech.model.entity.Speech;
import tech.challenge.speech.repository.SpeechRepository;
import tech.challenge.speech.repository.SpeechSpecification;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class SpeechService {

    private final SpeechRepository speechRepository;

    public SpeechService(SpeechRepository speechRepository) {
        this.speechRepository = speechRepository;
    }

    public SpeechDTO saveSpeech(SpeechDTO speechDTO) {
        Speech speechEntity = SpeechMapper.INSTANCE.toEntity(speechDTO);
        Speech savedSpeech = speechRepository.save(speechEntity);
        return SpeechMapper.INSTANCE.toDto(savedSpeech);
    }

    public void deleteSpeech(Long id) {
        if (!speechRepository.existsById(id)) {
            throw new NotFoundException("Speech not found with id " + id);
        }
        speechRepository.deleteById(id);
    }

    public List<SpeechDTO> searchSpeeches(String author, String snippet, OffsetDateTime startDate, OffsetDateTime endDate, Set<String> keywords) {
        // make keyword search case-insensitive
        final Set<String> lowerCaseKeywords = (keywords == null)
                ? Collections.emptySet()
                : keywords.stream().map(String::toLowerCase).collect(Collectors.toSet());

        final List<Speech> speeches = speechRepository.findAll(
                SpeechSpecification.filterSpeeches(author, snippet, startDate, endDate, lowerCaseKeywords)
        );

        if (speeches.isEmpty()) {
            throw new NotFoundException("No speeches found matching the search criteria");
        }

        return SpeechMapper.INSTANCE.toDtoList(speeches);
    }

    public SpeechDTO getSpeechById(Long id) {
        return speechRepository.findById(id)
                .map(SpeechMapper.INSTANCE::toDto)
                .orElseThrow(() -> new NotFoundException("Speech not found with id: " + id));
    }
}