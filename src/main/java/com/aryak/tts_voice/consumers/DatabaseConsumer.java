package com.aryak.tts_voice.consumers;

public interface DatabaseConsumer extends Runnable {
    void process();
    void stop();
}
