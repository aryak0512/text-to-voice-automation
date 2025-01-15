package com.aryak.tts_voice.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class TwilioService {

    public TwilioService() {
        Twilio.init(System.getenv("TWILIO_ACCOUNT_ID"), System.getenv("TWILIO_AUTH_TOKEN"));
    }

    public void makeVoiceCall(String toPhoneNumber, String audioUrl) {

        Call.creator(
                        new PhoneNumber(toPhoneNumber),
                        new PhoneNumber(System.getenv("TWILIO_PHONE_NUMBER")),
                        new com.twilio.type.Twiml("<Response><Play>" + audioUrl + "</Play></Response>")
                )
                ///6781-115-96-218-65.ngrok-free.app
                .setStatusCallback("https://6781-115-96-218-65.ngrok-free.app/twilio/call-status")
                .setStatusCallbackEvent(Arrays.asList("initiated", "ringing", "in-progress", "completed", "busy", "failed", "no-answer")).create();
    }

}
