package com.aryak.tts_voice.model;

public record VoiceRequest (
        String mobile,
        String message,
        String filename){}


