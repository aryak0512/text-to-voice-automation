package com.aryak.tts_voice.model;

import java.io.File;

public record UploadEvent(Event event,
                          File file) {
}
