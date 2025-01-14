package com.aryak.tts_voice.controller;

import com.aryak.tts_voice.model.VoiceRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.aryak.tts_voice.service.TextToSpeechService;
import com.aryak.tts_voice.service.TwilioService;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class VoiceController {

    @Autowired
    private TextToSpeechService textToSpeechService;

    @Autowired
    private TwilioService twilioService;

    @PostMapping("/call")
    public String makeVoiceCall(@RequestBody VoiceRequest voiceRequest) {

        try {

            String toPhoneNumber = "+91" + voiceRequest.mobile();
            String message = voiceRequest.message();
            String audioFile = voiceRequest.filename()+".mp3";

            String audioPath = textToSpeechService.convertTextToSpeech(message, audioFile);
            System.out.println("Audio path : " + audioPath);
            // push to google cloud bucket programmatically

            // Upload audio file to a public URL (e.g., AWS S3 or an accessible endpoint)
            String publicAudioUrl = "https://storage.googleapis.com/my-bucket-aryak/"+audioFile;

            twilioService.makeVoiceCall(toPhoneNumber, publicAudioUrl);
            return "Voice call initiated to " + toPhoneNumber;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
