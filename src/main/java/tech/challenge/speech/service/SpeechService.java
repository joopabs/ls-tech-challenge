package tech.challenge.speech.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.challenge.speech.exception.DuplicateSpeechException;
import tech.challenge.speech.exception.NotFoundException;
import tech.challenge.speech.mapper.SpeechMapper;
import tech.challenge.speech.model.dto.SpeechDTO;
import tech.challenge.speech.model.dto.UpdateSpeechDTO;
import tech.challenge.speech.model.entity.Speech;
import tech.challenge.speech.repository.SpeechRepository;
import tech.challenge.speech.repository.SpeechSpecification;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SpeechService {

    private final SpeechRepository speechRepository;

    public List<SpeechDTO> getAllSpeeches() {
        List<Speech> speeches = speechRepository.findAll();
        return SpeechMapper.INSTANCE.speechEntityToDtoList(speeches);
    }

    public SpeechDTO getSpeechById(Long id) {
        SpeechDTO dto = speechRepository.findById(id)
                .map(SpeechMapper.INSTANCE::toDto)
                .orElseThrow(() -> new NotFoundException("Speech not found with id: " + id));
        log.info("Found speech: {}", dto);
        return dto;
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
            log.warn("No speeches found matching the search criteria");
            throw new NotFoundException("No speeches found matching the search criteria");
        }

        return SpeechMapper.INSTANCE.speechEntityToDtoList(speeches);
    }

    public SpeechDTO saveSpeech(SpeechDTO speechDTO) {
        Speech speechEntity = SpeechMapper.INSTANCE.speechDtoToEntity(speechDTO);

        // Check for duplicates
        checkForDuplicate(speechEntity);

        Speech savedSpeech = speechRepository.save(speechEntity);
        return SpeechMapper.INSTANCE.toDto(savedSpeech);
    }

    public SpeechDTO updateSpeech(Long id, @Valid UpdateSpeechDTO updateSpeechDTO) {
        Speech existingSpeech = speechRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Speech not found with id: " + id));

        // Check for duplicates
        Speech forChecking = SpeechMapper.INSTANCE.updateSpeechToEntity(updateSpeechDTO);
        checkForDuplicate(forChecking);

        log.info("Current speech with id: {} to {}", id, existingSpeech);

        existingSpeech.setContent(updateSpeechDTO.getContent());
        existingSpeech.setAuthor(updateSpeechDTO.getAuthor());
        existingSpeech.setSpeechDate(updateSpeechDTO.getSpeechDate());
        existingSpeech.setKeywords(updateSpeechDTO.getKeywords());
        existingSpeech.setUpdateDateTime(OffsetDateTime.now());

        Speech updatedSpeech = speechRepository.save(existingSpeech);
        log.info("Updated speech with id: {} to {}", id, updatedSpeech);
        return SpeechMapper.INSTANCE.toDto(updatedSpeech);
    }

    public void deleteSpeech(Long id) {
        if (!speechRepository.existsById(id)) {
            log.warn("Speech not found with id {}", id);
            throw new NotFoundException("Speech not found with id " + id);
        }
        speechRepository.deleteById(id);
    }

    private void checkForDuplicate(Speech speech) {

        Optional<Speech> duplicate = speechRepository.findOne(
                SpeechSpecification.checkForDuplicate(speech)
        );

        if (duplicate.isPresent()) {
            log.warn("Found duplicate speech {}", duplicate.get());
            throw new DuplicateSpeechException("A speech with the same content, author, date, and keywords already exists.");
        }
    }
}