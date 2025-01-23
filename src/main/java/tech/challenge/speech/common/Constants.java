package tech.challenge.speech.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public final String SPEECHES_RETRIEVED = "Speech/es retrieved successfully";
    public final String SPEECH_CREATED = "Speech created successfully";
    public final String SPEECH_UPDATED = "Speech updated successfully";
    public final String SPEECH_DELETED = "Speech deleted successfully";
    public final String ID_CONFLICT_MESSAGE = "Conflict: ID in path does not match ID in request body";
}
