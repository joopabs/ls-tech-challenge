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

@Slf4j
@RestController
@RequestMapping("/api/speeches")
@RequiredArgsConstructor
public class SpeechController {

    private final SpeechService speechService;

    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<SpeechDTO>>> getAllSpeeches() {
        List<SpeechDTO> speeches = speechService.getAllSpeeches();
        log.info("Found {} speech/es.", speeches.size());
        return ResponseEntity.ok(new ApiResponseWrapper<>(
                HttpStatus.OK.value(),
                "Speeches retrieved successfully",
                speeches,
                null
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<SpeechDTO>> getSpeechById(@PathVariable Long id) {
        SpeechDTO speech = speechService.getSpeechById(id);
        log.info("Fetched speech with ID: {}", speech.getId());
        return ResponseEntity.ok(new ApiResponseWrapper<>(
                HttpStatus.OK.value(),
                "Speech retrieved successfully",
                speech,
                null
        ));
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
        return ResponseEntity.ok(new ApiResponseWrapper<>(
                HttpStatus.OK.value(),
                "Speeches retrieved successfully",
                speeches,
                null));
    }

    @PostMapping
    public ResponseEntity<ApiResponseWrapper<SpeechDTO>> createSpeech(@Valid @RequestBody SpeechDTO speechDTO) {
        SpeechDTO savedDTO = speechService.saveSpeech(speechDTO);
        log.info("Created new speech with ID: {}", savedDTO.getId());
        return new ResponseEntity<>(new ApiResponseWrapper<>(
                HttpStatus.CREATED.value(),
                "Speech created successfully",
                savedDTO,
                null),
                HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<SpeechDTO>> updateSpeech(
            @PathVariable Long id, @Valid @RequestBody UpdateSpeechDTO updateSpeechDTO) {

        // Check if the path ID matches the body ID
        if (!id.equals(updateSpeechDTO.getId())) {
            log.warn("Conflict: Path ID ({}) does not match request body ID ({}).", id, updateSpeechDTO.getId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ApiResponseWrapper<>(
                            HttpStatus.CONFLICT.value(),
                            "Conflict: ID in path does not match ID in request body",
                            null,
                            null
                    )
            );
        }

        SpeechDTO updatedSpeech = speechService.updateSpeech(id, updateSpeechDTO);
        log.info("Updated speech with ID: {}", id);
        return ResponseEntity.ok(
                new ApiResponseWrapper<>(
                        HttpStatus.OK.value(),
                        "Speech updated successfully",
                        updatedSpeech,
                        null
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteSpeech(@PathVariable Long id) {
        speechService.deleteSpeech(id);
        log.info("Deleted speech with ID: {}", id);
        return ResponseEntity.ok(new ApiResponseWrapper<>(
                HttpStatus.OK.value(),
                "Speech deleted successfully",
                null,
                null));
    }
}