package com.aryak.tts_voice.model;

import java.util.List;

public record VoiceRequest(
        String mobile,
        String countryCode,
        String message,
        String filename,
        boolean hindi) {
}


