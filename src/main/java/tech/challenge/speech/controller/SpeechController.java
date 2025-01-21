package tech.challenge.speech.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/speeches")
public class SpeechController {
    @GetMapping
    public String sayHello() {
        return "Hello, World!";
    }
}
