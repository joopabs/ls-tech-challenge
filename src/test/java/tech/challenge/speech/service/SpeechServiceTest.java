package tech.challenge.speech.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;
import tech.challenge.speech.exception.DuplicateSpeechException;
import tech.challenge.speech.exception.NotFoundException;
import tech.challenge.speech.model.dto.SpeechDTO;
import tech.challenge.speech.model.dto.UpdateSpeechDTO;
import tech.challenge.speech.model.entity.Speech;
import tech.challenge.speech.repository.SpeechRepository;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SpeechServiceTest {

    private SpeechRepository speechRepository;
    private SpeechService speechService;

    @BeforeEach
    void setUp() {
        speechRepository = mock(SpeechRepository.class);
        speechService = new SpeechService(speechRepository);
    }

    @Test
    void shouldCallFindAllOnGetAllSpeeches() {
        speechService.getAllSpeeches();

        verify(speechRepository).findAll();
    }

    @Test
    void shouldCallFindByIdOnGetSpeechById() {
        Long speechId = 1L;
        when(speechRepository.findById(speechId)).thenReturn(Optional.of(new Speech()));

        speechService.getSpeechById(speechId);

        verify(speechRepository).findById(speechId);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenGetSpeechByIdNotFound() {
        Long speechId = 1L;
        when(speechRepository.findById(speechId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> speechService.getSpeechById(speechId));
        verify(speechRepository).findById(speechId);
    }

    @Test
    void shouldCallFindAllWithSpecificationOnSearchSpeeches() {
        String author = "John Doe";
        String snippet = "sample";
        OffsetDateTime startDate = OffsetDateTime.now();
        OffsetDateTime endDate = OffsetDateTime.now().plusDays(1);
        Set<String> keywords = Set.of("keyword");
        when(speechRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(new Speech()));

        speechService.searchSpeeches(author, snippet, startDate, endDate, keywords);

        verify(speechRepository).findAll(any(Specification.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenNoSpeechesFoundWithGivenSearchCriteria() {
        String author = "John Doe";
        String snippet = "sample";
        OffsetDateTime startDate = OffsetDateTime.now();
        OffsetDateTime endDate = OffsetDateTime.now().plusDays(1);
        Set<String> keywords = Set.of("keyword");
        when(speechRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () ->
                speechService.searchSpeeches(author, snippet, startDate, endDate, keywords));

        verify(speechRepository).findAll(any(Specification.class));
    }

    @Test
    void shouldCallSaveOnSaveSpeech() {
        SpeechDTO speechDTO = new SpeechDTO();
        Speech speech = new Speech();
        when(speechRepository.save(any(Speech.class))).thenReturn(speech);
        doReturn(Optional.empty()).when(speechRepository).findOne(any(Specification.class));

        speechService.saveSpeech(speechDTO);

        verify(speechRepository).save(any(Speech.class));
    }

    @Test
    void shouldThrowDuplicateSpeechExceptionOnSaveSpeechIfDuplicateExists() {
        SpeechDTO speechDTO = new SpeechDTO();
        Speech speech = new Speech();
        when(speechRepository.findOne(any(Specification.class))).thenReturn(Optional.of(speech));

        assertThrows(DuplicateSpeechException.class, () -> speechService.saveSpeech(speechDTO));
        verify(speechRepository, never()).save(speech);
    }

    @Test
    void shouldCallFindByIdAndSaveOnUpdateSpeech() {
        Long speechId = 1L;
        UpdateSpeechDTO updateSpeechDTO = new UpdateSpeechDTO();
        Speech speech = new Speech();
        when(speechRepository.findById(speechId)).thenReturn(Optional.of(speech));
        doReturn(Optional.empty()).when(speechRepository).findOne(any(Specification.class));
        when(speechRepository.save(any(Speech.class))).thenReturn(speech);

        speechService.updateSpeech(speechId, updateSpeechDTO);

        verify(speechRepository).findById(speechId);
        verify(speechRepository).save(any(Speech.class));
    }

    @Test
    void shouldThrowNotFoundExceptionOnUpdateSpeechIfSpeechDoesNotExist() {
        Long speechId = 1L;
        UpdateSpeechDTO updateSpeechDTO = new UpdateSpeechDTO();

        when(speechRepository.findById(speechId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> speechService.updateSpeech(speechId, updateSpeechDTO));
        verify(speechRepository).findById(speechId);
        verify(speechRepository, never()).save(any(Speech.class));
    }

    @Test
    void shouldThrowDuplicateSpeechExceptionOnUpdateSpeechIfDuplicateExists() {
        Long speechId = 1L;
        UpdateSpeechDTO updateSpeechDTO = new UpdateSpeechDTO();
        Speech speech = new Speech();
        when(speechRepository.findById(speechId)).thenReturn(Optional.of(speech));
        when(speechRepository.findOne(any(Specification.class))).thenReturn(Optional.of(speech));

        assertThrows(DuplicateSpeechException.class, () -> speechService.updateSpeech(speechId, updateSpeechDTO));
        verify(speechRepository).findById(speechId);
        verify(speechRepository, never()).save(any(Speech.class));
    }

    @Test
    void shouldCallExistsByIdAndDeleteForDeleteSpeech() {
        Long speechId = 1L;
        when(speechRepository.existsById(speechId)).thenReturn(true);

        speechService.deleteSpeech(speechId);

        verify(speechRepository).existsById(speechId);
        verify(speechRepository).deleteById(speechId);
    }

    @Test
    void shouldThrowNotFoundExceptionIfSpeechDoesNotExistOnDeleteSpeech() {
        Long speechId = 1L;
        when(speechRepository.existsById(speechId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> speechService.deleteSpeech(speechId));
        verify(speechRepository).existsById(speechId);
        verify(speechRepository, never()).deleteById(speechId);
    }
}