package com.aryak.tts_voice.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/twilio")
public class TwilioWebhookController {

    @PostMapping("/call-status")
    public void handleCallStatus(HttpServletRequest request) {
        // Parse the incoming Twilio request
        Map<String, String[]> params = request.getParameterMap();

        // Get important details
        String callSid = params.get("CallSid")[0]; // Unique identifier for the call
        String callStatus = params.get("CallStatus")[0]; // Status of the call (e.g., "completed", "no-answer")

        System.out.println("Call SID: " + callSid);
        System.out.println("Call Status: " + callStatus);

        // Process the status update (e.g., log it, update your database, etc.)
    }
}
