package tech.challenge.speech.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import tech.challenge.speech.model.dto.SpeechDTO;
import tech.challenge.speech.model.entity.Speech;


@Mapper
public interface SpeechMapper {

    SpeechMapper INSTANCE = Mappers.getMapper(SpeechMapper.class);

    SpeechDTO toDto(Speech speech);

    Speech toEntity(SpeechDTO speechDTO);
}