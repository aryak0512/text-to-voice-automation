package com.aryak.tts_voice.controller;

import com.aryak.tts_voice.model.VoiceRequest;
import com.aryak.tts_voice.repo.EventRepository;
import com.aryak.tts_voice.service.GCSUploaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import com.aryak.tts_voice.service.TextToSpeechService;
import com.aryak.tts_voice.service.TwilioService;

import java.io.File;
import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class VoiceController {

    private static final Logger log = LoggerFactory.getLogger(VoiceController.class);
    private final TwilioService twilioService;
    private final TextToSpeechService textToSpeechService;
    private final GCSUploaderService gcsUploaderService;
    private final EventRepository eventRepository;

    public VoiceController(TwilioService twilioService, TextToSpeechService textToSpeechService, GCSUploaderService gcsUploaderService, EventRepository eventRepository) {
        this.twilioService = twilioService;
        this.textToSpeechService = textToSpeechService;
        this.gcsUploaderService = gcsUploaderService;
        this.eventRepository = eventRepository;
    }

    @PostMapping("/call")
    public String makeVoiceCall(@RequestBody VoiceRequest voiceRequest) {

        log.info("Request received for voice call : {}", voiceRequest);

        try {

            String toPhoneNumber = voiceRequest.countryCode() + voiceRequest.mobile();
            String message = voiceRequest.message();
            String audioFile = voiceRequest.filename() + ".mp3";

            File file = textToSpeechService.convertTextToSpeech(message, audioFile, voiceRequest.hindi());
            String fileName = file.getName();
            String url = "https://storage.googleapis.com/" + System.getenv("BUCKET_NAME") + "/" + audioFile;

            // push to google cloud bucket programmatically
            gcsUploaderService.uploadFile(System.getenv("BUCKET_NAME"), fileName, file);
            log.info("File uploaded to URL : {}", url);
            twilioService.makeVoiceCall(toPhoneNumber, url);
            log.info("Twilio request success.");
            return "Voice call initiated to " + toPhoneNumber;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/bulk")
    public String makeBulkVoiceCalls(@RequestBody VoiceRequest voiceRequest) {

        log.info("Request bulk received for voice call : {}", voiceRequest);

        try {

            String countryCode = voiceRequest.countryCode();
            var mobileNumbers = Arrays.stream(voiceRequest.mobile().split(",")).map(countryCode::concat).toList();
            log.info("Mobile numbers : {}", mobileNumbers);
            String message = voiceRequest.message();
            String audioFile = voiceRequest.filename() + ".mp3";

            File file = textToSpeechService.convertTextToSpeech(message, audioFile, voiceRequest.hindi());
            String fileName = file.getName();
            String url = "https://storage.googleapis.com/" + System.getenv("BUCKET_NAME") + "/" + audioFile;

            // push to google cloud bucket programmatically
            gcsUploaderService.uploadFile(System.getenv("BUCKET_NAME"), fileName, file);
            mobileNumbers.parallelStream().forEach(number -> twilioService.makeVoiceCall(number, url));
            return "Voice calls initiated";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping(value = "/v1/trigger/{eventId}")
    public void triggerAlert(@PathVariable int eventId) {

        var event = eventRepository.findByEventId(eventId);

        // retrieve public bucket url
        String audioFile = event.fileName();
        String url = "https://storage.googleapis.com/" + System.getenv("BUCKET_NAME") + "/" + audioFile;

        log.info("Url : {}", url);
        // prepare list of mobile numbers from event config
        var mobileNumbers = event.receivers()
                .stream()
                .map(r -> r.countryCode().concat(r.mobile()))
                .filter(n -> ! n.equals("+919769643044"))  // remove blacklist/ DND customers
                .toList();

        log.info("Prepared mobile numbers list : {}", mobileNumbers);

        // prepare voice requests & fire multithreaded
        mobileNumbers.parallelStream().forEach(number -> twilioService.makeVoiceCall(number, url));

    }
}
