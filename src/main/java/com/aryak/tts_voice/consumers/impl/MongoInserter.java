package com.aryak.tts_voice.consumers.impl;

import com.aryak.tts_voice.config.QueueManager;
import com.aryak.tts_voice.consumers.DatabaseConsumer;
import com.aryak.tts_voice.model.Event;
import com.aryak.tts_voice.repo.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MongoInserter implements DatabaseConsumer {

    private static final Logger log = LoggerFactory.getLogger(MongoInserter.class);

    private final EventRepository eventRepository;

    private final QueueManager queueManager;

    private volatile boolean running = true;

    public MongoInserter(EventRepository eventRepository, QueueManager queueManager) {
        this.eventRepository = eventRepository;
        this.queueManager = queueManager;
    }

    @Override
    public void run() {
        while (running) {
            process();
        }
    }

    @Override
    public void process() {
        try {
            Event event = queueManager.getDbQueue().take();
            eventRepository.save(event);
            log.info("Step 3 done - Event metadata saved to database: {}", event.fileName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Metadata Consumer interrupted: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error during metadata saving: {}", e.getMessage());
        }
    }

    public void stop() {
        running = false;
    }
}
