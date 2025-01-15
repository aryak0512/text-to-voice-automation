package com.aryak.tts_voice.service;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class TextToSpeechService {

    public String convertTextToSpeech(String text, String outputFile) throws IOException {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            // Set the text input
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            // Select the voice and language
//            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
//                    .setLanguageCode("en-US") // Change language if needed
//                    .setSsmlGender(SsmlVoiceGender.MALE)
//                    .build();

            // Build the voice request, select the language code and voice name
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("hi-IN") // Hindi
                    .setName("hi-IN-Wavenet-A") // Change to desired voice
                    .build();

            // Set audio configuration
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            // Perform the TTS request
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Save the audio file
            ByteString audioContents = response.getAudioContent();
            String path = "./files/"+ outputFile;
            try (FileOutputStream out = new FileOutputStream(path)) {
                out.write(audioContents.toByteArray());
            }
            return path;
        }
    }
}
