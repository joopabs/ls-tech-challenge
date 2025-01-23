package tech.challenge.speech.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.challenge.speech.model.dto.ApiResponseWrapper;
import tech.challenge.speech.model.dto.SpeechDTO;
import tech.challenge.speech.model.dto.UpdateSpeechDTO;
import tech.challenge.speech.service.SpeechService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static tech.challenge.speech.common.ApiResponseBuilder.buildResponse;
import static tech.challenge.speech.common.Constants.*;

@Slf4j
@RestController
@RequestMapping("/api/speeches")
@RequiredArgsConstructor
public class SpeechController {

    private final SpeechService speechService;

    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<SpeechDTO>>> getAllSpeeches() {
        List<SpeechDTO> allSpeeches = speechService.getAllSpeeches();
        log.info("Found {} speech/es.", allSpeeches.size());
        return buildResponse(HttpStatus.OK, SPEECHES_RETRIEVED, allSpeeches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<SpeechDTO>> getSpeechById(@PathVariable Long id) {
        SpeechDTO speech = speechService.getSpeechById(id);
        log.info("Fetched speech with ID: {}", id);
        return buildResponse(HttpStatus.OK, SPEECHES_RETRIEVED, speech);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseWrapper<List<SpeechDTO>>> searchSpeeches(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String snippet,
            @RequestParam(required = false) OffsetDateTime startDate,
            @RequestParam(required = false) OffsetDateTime endDate,
            @RequestParam(required = false) Set<String> keywords
    ) {
        List<SpeechDTO> speeches = speechService.searchSpeeches(author, snippet, startDate, endDate, keywords);
        log.info("Found {} speech/es.", speeches.size());
        return buildResponse(HttpStatus.OK, SPEECHES_RETRIEVED, speeches);
    }

    @PostMapping
    public ResponseEntity<ApiResponseWrapper<SpeechDTO>> createSpeech(@Valid @RequestBody SpeechDTO speechDTO) {
        SpeechDTO createdSpeech = speechService.saveSpeech(speechDTO);
        log.info("Created new speech with ID: {}", createdSpeech.getId());
        return buildResponse(HttpStatus.CREATED, SPEECH_CREATED, createdSpeech);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<SpeechDTO>> updateSpeech(
            @PathVariable Long id, @Valid @RequestBody UpdateSpeechDTO updateSpeechDTO) {
        if (!id.equals(updateSpeechDTO.getId())) {
            log.warn("Conflict: Path ID ({}) does not match request body ID ({}).", id, updateSpeechDTO.getId());
            return buildResponse(HttpStatus.CONFLICT, ID_CONFLICT_MESSAGE, null);
        }
        SpeechDTO updatedSpeech = speechService.updateSpeech(id, updateSpeechDTO);
        log.info("Updated speech with ID: {}", id);
        return buildResponse(HttpStatus.OK, SPEECH_UPDATED, updatedSpeech);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteSpeech(@PathVariable Long id) {
        speechService.deleteSpeech(id);
        log.info("Deleted speech with ID: {}", id);
        return buildResponse(HttpStatus.OK, SPEECH_DELETED, null);
    }
}