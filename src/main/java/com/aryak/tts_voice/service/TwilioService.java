package com.aryak.tts_voice.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    public TwilioService() {
        Twilio.init(System.getenv("TWILIO_ACCOUNT_ID"), System.getenv("TWILIO_AUTH_TOKEN"));
    }

    public void makeVoiceCall(String toPhoneNumber, String audioUrl) {

        Call call = Call.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(System.getenv("TWILIO_PHONE_NUMBER")),
                new com.twilio.type.Twiml("<Response><Play>" + audioUrl + "</Play></Response>")
        ).create();
    }

}
