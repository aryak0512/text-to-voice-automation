package com.aryak.tts_voice.consumers;

public interface UploadConsumer extends Runnable {

    void process();

    void stop();
}
