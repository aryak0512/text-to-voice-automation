package com.aryak.tts_voice.consumers;

public interface ConverterConsumer extends Runnable {
    void process();
    void stop();
}
