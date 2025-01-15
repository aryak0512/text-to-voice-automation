package com.aryak.tts_voice.controller;

import com.aryak.tts_voice.model.Event;
import com.aryak.tts_voice.repo.EventRepository;
import com.aryak.tts_voice.service.GCSUploaderService;
import com.aryak.tts_voice.service.TextToSpeechService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final EventRepository eventRepository;

    private final TextToSpeechService textToSpeechService;

    private final GCSUploaderService uploaderService;

    public EventController(EventRepository eventRepository, TextToSpeechService textToSpeechService, GCSUploaderService uploaderService) {
        this.eventRepository = eventRepository;
        this.textToSpeechService = textToSpeechService;
        this.uploaderService = uploaderService;
    }

    /**
     * need to decouple these individual operations inside this function by using message queues
     * @param config
     */
    @PostMapping(value = "/add")
    public void addConfig(@RequestBody Event config) {

        File file = null;

        // task 1 - do the text to speech conversion and save file on disk
        try {
            file = textToSpeechService.convertTextToSpeech(config.message(), config.fileName(), config.hindi());
        } catch (Exception e) {
            log.error("Exception occurred during converting from speech to text : ", e);
        }

        log.info("Step 1 done");

        // task 2 - upload file to Google cloud bucket
        try {
            uploaderService.uploadFile(System.getenv("BUCKET_NAME"), config.fileName(), file);
        } catch (Exception e) {
            log.error("Exception occurred while uploading file to GCP bucket : ", e.getCause());
        }

        log.info("Step 2 done");

        // task 3 - then store metadata of event to db
        eventRepository.save(config);
        log.info("Step 3 done");

    }

    @GetMapping(value = "/")
    public List<Event> getConfig() {
        return eventRepository.findAll();
    }

    @Operation(summary = "Get user by ID", description = "Retrieve an event by their ID")
    @GetMapping(value = "/{id}")
    public Event getConfig(@PathVariable(name = "id") int eventId) {
        return eventRepository.findByEventId(eventId);
    }
}
