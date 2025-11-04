package com.nayabas.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender; // 🛑 NEW IMPORT
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    // 🛑 Inject the real MailSender provided by Spring Boot
    private final JavaMailSender mailSender; 
    private final com.nayabas.config.NotificationConfig config;

    /**
     * Sends an email using JavaMailSender.
     */
    public void sendEmail(String recipientEmail, String subject, String body) {
        if (recipientEmail == null || recipientEmail.isEmpty()) {
            System.err.println("EmailService: Failed - Recipient email is null or empty.");
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(config.getSenderEmailAddress()); // Uses the configured sender email
            message.setTo(recipientEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message); // 🛑 REAL EMAIL DISPATCH

            System.out.println("--- EMAIL DISPATCH ---");
            System.out.println("SENDER: " + config.getSenderEmailAddress());
            System.out.println("RECIPIENT: " + recipientEmail);
            System.out.println("SUBJECT: " + subject);
            System.out.println("STATUS: SENT via live SMTP server.");
            System.out.println("----------------------");

        } catch (Exception e) {
            System.err.println("EmailService: FAILED to send email to " + recipientEmail + ". Error: " + e.getMessage());
            // Log the exception in a real system (e.g., logger.error(...))
        }
    }
}