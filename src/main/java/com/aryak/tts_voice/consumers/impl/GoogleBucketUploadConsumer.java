package com.aryak.tts_voice.consumers.impl;

import com.aryak.tts_voice.config.QueueManager;
import com.aryak.tts_voice.consumers.UploadConsumer;
import com.aryak.tts_voice.model.UploadEvent;
import com.aryak.tts_voice.service.GCSUploaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GoogleBucketUploadConsumer implements UploadConsumer {

    private static final Logger log = LoggerFactory.getLogger(GoogleBucketUploadConsumer.class);

    private final QueueManager queueManager;

    private final GCSUploaderService uploaderService;

    private volatile boolean running = true;

    public GoogleBucketUploadConsumer(QueueManager queueManager, GCSUploaderService uploaderService) {
        this.queueManager = queueManager;
        this.uploaderService = uploaderService;
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
            UploadEvent uploadEvent = queueManager.getBucketQueue().take();
            uploaderService.uploadFile(System.getenv("BUCKET_NAME"), uploadEvent.file().getName(), uploadEvent.file());
            log.info("Step 2 done - File uploaded to GCP bucket: {}", uploadEvent.file().getName());
            queueManager.getDbQueue().put(uploadEvent.event());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Upload Consumer interrupted: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error during file upload: {}", e.getMessage());
        }
    }

    public void stop() {
        running = false;
    }
}
