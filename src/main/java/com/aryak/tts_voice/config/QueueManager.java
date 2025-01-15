package com.aryak.tts_voice.config;

import com.aryak.tts_voice.model.Event;
import com.aryak.tts_voice.model.UploadEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class QueueManager {

    ExecutorService executorService = Executors.newFixedThreadPool(3);
    private final BlockingQueue<Event> textToSpeechQueue;
    private final BlockingQueue<Event> dbQueue;
    private final BlockingQueue<UploadEvent> bucketQueue;

    public QueueManager() {
        textToSpeechQueue = new LinkedBlockingQueue<>();
        dbQueue = new LinkedBlockingQueue<>();
        bucketQueue = new LinkedBlockingQueue<>();
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public BlockingQueue<UploadEvent> getBucketQueue() {
        return bucketQueue;
    }

    public BlockingQueue<Event> getTextToSpeechQueue() {
        return textToSpeechQueue;
    }

    public BlockingQueue<Event> getDbQueue() {
        return dbQueue;
    }
}
