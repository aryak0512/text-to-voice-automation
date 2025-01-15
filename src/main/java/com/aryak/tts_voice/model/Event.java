package com.aryak.tts_voice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(value = "event_config")
public record Event(List<Receiver> receivers, String message, @Id int eventId, String fileName, boolean hindi) {

}
