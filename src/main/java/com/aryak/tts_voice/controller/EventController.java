package com.aryak.tts_voice.controller;

import com.aryak.tts_voice.config.QueueManager;
import com.aryak.tts_voice.model.Event;
import com.aryak.tts_voice.repo.EventRepository;
import com.aryak.tts_voice.service.GCSUploaderService;
import com.aryak.tts_voice.service.TextToSpeechService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author aryak
 *
 * API to trigger voice message to handsets
 */
@RestController
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final EventRepository eventRepository;

    private final QueueManager queueManager;

    public EventController(EventRepository eventRepository, QueueManager queueManager) {
        this.eventRepository = eventRepository;
        this.queueManager = queueManager;
    }

    /**
     * need to decouple these individual operations inside this function by using message queues
     * @param config
     */
    @PostMapping(value = "/add")
    public ResponseEntity<String> addConfig(@RequestBody Event config) {
        boolean success = queueManager.getTextToSpeechQueue().add(config);
        log.info("Add operation gave : {}", success);
        return new ResponseEntity<>("Your request has been taken up for processing.", HttpStatus.CREATED);
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
