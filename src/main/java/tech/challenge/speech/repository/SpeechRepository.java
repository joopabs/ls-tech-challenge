package tech.challenge.speech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tech.challenge.speech.model.entity.Speech;

public interface SpeechRepository extends JpaRepository<Speech, Long>, JpaSpecificationExecutor<Speech> {
}