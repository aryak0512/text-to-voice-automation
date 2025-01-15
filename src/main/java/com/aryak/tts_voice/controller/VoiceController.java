package com.aryak.tts_voice.controller;

import com.aryak.tts_voice.model.VoiceRequest;
import com.aryak.tts_voice.service.GCSUploaderService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import com.aryak.tts_voice.service.TextToSpeechService;
import com.aryak.tts_voice.service.TwilioService;

import java.io.File;

@RestController
@RequestMapping("/api")
public class VoiceController {

    private static final Logger log = LoggerFactory.getLogger(VoiceController.class);
    private final TwilioService twilioService;
    private final TextToSpeechService textToSpeechService;
    private final GCSUploaderService gcsUploaderService;

    public VoiceController(TwilioService twilioService, TextToSpeechService textToSpeechService, GCSUploaderService gcsUploaderService) {
        this.twilioService = twilioService;
        this.textToSpeechService = textToSpeechService;
        this.gcsUploaderService = gcsUploaderService;
    }

    @PostMapping("/call")
    public String makeVoiceCall(@RequestBody VoiceRequest voiceRequest) {

        log.info("Request received for voice call : {}", voiceRequest);

        try {

            String toPhoneNumber = voiceRequest.countryCode() + voiceRequest.mobile();
            String message = voiceRequest.message();
            String audioFile = voiceRequest.filename() +".mp3";

            String audioPath = textToSpeechService.convertTextToSpeech(message, audioFile);
            File file = new File(audioPath);
            String fileName = file.getName();
            String url = "https://storage.googleapis.com/"+System.getenv("BUCKET_NAME")+"/"+ audioFile;

            // push to google cloud bucket programmatically
            gcsUploaderService.uploadFile("my-bucket-aryak", fileName, file);
            log.info("File uploaded to URL : {}", url);
            twilioService.makeVoiceCall(toPhoneNumber, url);
            log.info("Twilio request success.");
            return "Voice call initiated to " + toPhoneNumber;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
