package tech.challenge.speech.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.challenge.speech.entity.Speech;
import tech.challenge.speech.service.SpeechService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/speeches")
public class SpeechController {

    private final SpeechService speechService;

    public SpeechController(SpeechService speechService) {
        this.speechService = speechService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<Speech>> searchSpeeches(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String snippet,
            @RequestParam(required = false) OffsetDateTime startDate,
            @RequestParam(required = false) OffsetDateTime endDate,
            @RequestParam(required = false) Set<String> keywords
    ) {
        List<Speech> speeches = speechService.searchSpeeches(author, snippet, startDate, endDate, keywords);
        return ResponseEntity.ok(speeches);
    }
}