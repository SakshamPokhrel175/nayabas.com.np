package com.nayabas.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class NotificationConfig {

    // --- Email Service Configuration (e.g., SendGrid/SES)
    @Value("${notification.email.apiKey}")
    private String emailApiKey;

    @Value("${notification.email.senderAddress}")
    private String senderEmailAddress;

    // --- SMS/WhatsApp Service Configuration (e.g., Twilio)
    @Value("${notification.messaging.accountSid}")
    private String twilioAccountSid;

    @Value("${notification.messaging.authToken}")
    private String twilioAuthToken;

    @Value("${notification.messaging.fromNumber}")
    private String twilioFromNumber;
}