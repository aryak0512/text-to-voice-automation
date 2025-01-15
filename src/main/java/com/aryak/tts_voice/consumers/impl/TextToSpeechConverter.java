package com.aryak.tts_voice.consumers.impl;

import com.aryak.tts_voice.config.QueueManager;
import com.aryak.tts_voice.consumers.ConverterConsumer;
import com.aryak.tts_voice.model.Event;
import com.aryak.tts_voice.model.UploadEvent;
import com.aryak.tts_voice.service.TextToSpeechService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class TextToSpeechConverter implements ConverterConsumer {

    private static final Logger log = LoggerFactory.getLogger(TextToSpeechConverter.class);

    private final TextToSpeechService textToSpeechService;

    private final QueueManager queueManager;

    private volatile boolean running = true;

    public TextToSpeechConverter(TextToSpeechService textToSpeechService, QueueManager queueManager) {
        this.textToSpeechService = textToSpeechService;
        this.queueManager = queueManager;
    }

    @Override
    public void run() {
        while ( running ) {
            process();
        }
    }

    @Override
    public void process() {
        try {
            Event config = queueManager.getTextToSpeechQueue().take();
            File file = textToSpeechService.convertTextToSpeech(config.message(), config.fileName(), config.hindi());
            log.info("Step 1 done - Text-to-Speech conversion completed for: {}", config.fileName());
            UploadEvent uploadEvent = new UploadEvent(config, file);
            queueManager.getBucketQueue().put(uploadEvent);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Text-to-Speech Consumer interrupted: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error during Text-to-Speech processing: {}", e.getMessage());
        }
    }

    public void stop() {
        running = false;
    }
}
