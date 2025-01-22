package tech.challenge.speech.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.challenge.speech.model.dto.ApiResponse;
import tech.challenge.speech.model.dto.SpeechDTO;
import tech.challenge.speech.model.entity.Speech;
import tech.challenge.speech.service.SpeechService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/speeches")
@RequiredArgsConstructor
public class SpeechController {

    private final SpeechService speechService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Speech>>> searchSpeeches(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String snippet,
            @RequestParam(required = false) OffsetDateTime startDate,
            @RequestParam(required = false) OffsetDateTime endDate,
            @RequestParam(required = false) Set<String> keywords
    ) {
        List<Speech> speeches = speechService.searchSpeeches(author, snippet, startDate, endDate, keywords);
        return ResponseEntity.ok(new ApiResponse<>(
                HttpStatus.OK.value(),
                "Speeches retrieved successfully",
                speeches,
                null));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SpeechDTO>> createSpeech(@Valid @RequestBody SpeechDTO speechDTO) {
        SpeechDTO savedDTO = speechService.saveSpeech(speechDTO);
        return new ResponseEntity<>(new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Speech created successfully",
                savedDTO,
                null),
                HttpStatus.CREATED);
    }
}