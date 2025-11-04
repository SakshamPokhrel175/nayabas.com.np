package com.nayabas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagingService {

    private final com.nayabas.config.NotificationConfig config;

    /**
     * Cleans and formats a phone number into the E.164 standard (+CountryCodeNumber).
     * Necessary for Twilio/WhatsApp/SMS APIs.
     * @param rawNumber The raw phone number string from the User entity.
     * @return Formatted E.164 string or null if invalid.
     */
    private String formatPhoneNumber(String rawNumber) {
        if (rawNumber == null || rawNumber.isEmpty()) {
            return null;
        }
        // Remove all non-digit characters except for a leading '+'
        String cleaned = rawNumber.replaceAll("[^\\d+]", "");
        
        // Ensure it starts with a country code (Twilio requires leading '+')
        if (!cleaned.startsWith("+")) {
            // WARNING: This assumes a default country code if none is present.
            // For a real app, you must store country code separately, but for now:
            // Assuming default code (e.g., +91 for India if not specified)
            // Since we don't know the default, we return null, forcing user to input country code.
            System.err.println("MessagingService: Number missing country code. Cannot send SMS/WA.");
            return null;
        }

        // Basic validation check (must be more robust in production)
        if (cleaned.length() < 10) {
             return null;
        }
        
        return cleaned;
    }

    /**
     * Placeholder method for sending SMS/WhatsApp.
     * Replace the System.out with Twilio/equivalent SDK logic.
     */
    public void sendSms(String recipientPhoneNumber, String message) {
        String formattedNumber = formatPhoneNumber(recipientPhoneNumber);
        
        if (formattedNumber == null) {
            System.err.println("MessagingService: Failed - Invalid phone number format.");
            return;
        }

        System.out.println("--- SMS/WA DISPATCH ---");
        System.out.println("SERVICE: SMS");
        System.out.println("FROM: " + config.getTwilioFromNumber());
        System.out.println("TO: " + formattedNumber);
        System.out.println("STATUS: SENT to external API (Simulated)");
        System.out.println("-----------------------");

        // 💡 REAL IMPLEMENTATION GOES HERE:
        // Example: Twilio.init(sid, token); Message.creator(to, from, body).create();
    }
}