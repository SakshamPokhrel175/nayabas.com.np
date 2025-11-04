package com.nayabas.service;

import com.nayabas.entity.Booking;
import com.nayabas.entity.Meeting;
import com.nayabas.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.hibernate.Hibernate; // Ensure this import is present

@Service
@RequiredArgsConstructor
public class NotificationService {

    // 🛑 INJECTED REAL SERVICES (Lombok creates constructor for these 'final' fields)
    private final EmailService emailService;
    private final MessagingService messagingService;

    // ============================================================
    // Public Dispatch Methods (Called by MeetingService/BookingService)
    // ============================================================

    public void sendMeetingUpdateNotification(Meeting meeting, String eventType) {
        User customer = meeting.getCustomer();
        User seller = meeting.getProperty().getOwner();

        // 1. Build messages for the Customer
        String customerSubject = String.format("Meeting Status Update: %s for %s", eventType, meeting.getProperty().getTitle());
        String customerBody = buildMeetingMessage(meeting, eventType, "customer");
        
        // 2. Build messages for the Seller
        String sellerSubject = String.format("Buyer Action: %s for %s", eventType, meeting.getProperty().getTitle());
        String sellerBody = buildMeetingMessage(meeting, eventType, "seller");

        // 3. Dispatch Logic
        if (eventType.equals("PENDING")) {
            // Customer requested -> Notify Seller
            dispatchAllChannels(seller, sellerSubject, sellerBody);
        } else if (eventType.equals("SCHEDULED")) {
            // Final confirmation reached
            dispatchAllChannels(customer, customerSubject, customerBody);
            dispatchAllChannels(seller, sellerSubject, sellerBody);
        } else if (eventType.equals("PROPOSED_CHANGE")) {
            // Seller proposed a change -> Notify ONLY Customer (action required)
            dispatchAllChannels(customer, customerSubject, customerBody);
        } else if (eventType.equals("REJECTED")) {
            // Rejection by either party
            dispatchAllChannels(customer, customerSubject, customerBody);
            dispatchAllChannels(seller, sellerSubject, sellerBody);
        }
    }
    
    public void sendBookingUpdateNotification(Booking booking, String eventType) {
        User customer = booking.getCustomer();
        User seller = booking.getProperty().getOwner();
        
        String subject = String.format("Booking Status: %s for %s", eventType, booking.getProperty().getTitle());
        String body = buildBookingMessage(booking, eventType);

        if (eventType.equals("PENDING")) {
            dispatchAllChannels(seller, subject, body);
        } else if (eventType.equals("APPROVED") || eventType.equals("REJECTED")) {
            dispatchAllChannels(customer, subject, body);
        }
    }

    // ============================================================
    // Private Generic Dispatch (REAL Dispatch)
    // ============================================================
    
    private void dispatchAllChannels(User recipient, String subject, String body) {
        // Log the event to console
        System.out.println("--- NOTIFICATION DISPATCH (TO: " + recipient.getEmail() + ") ---");

        // 1. Email Dispatch
        emailService.sendEmail(recipient.getEmail(), subject, body);

        // 2. SMS/WhatsApp Dispatch
        if (recipient.getPhoneNumber() != null && !recipient.getPhoneNumber().isEmpty()) {
             messagingService.sendSms(recipient.getPhoneNumber(), body);
        } else {
             System.err.println("MessagingService: Recipient has no phone number. SMS/WA skipped.");
        }
    }
    
    // ============================================================
    // Message Builders (Detailed Content)
    // ============================================================
    
    private String buildMeetingMessage(Meeting m, String eventType, String recipientRole) {
        // Initialize Property and Owner for safe access to EAGER fields
        Hibernate.initialize(m.getProperty());
        Hibernate.initialize(m.getProperty().getOwner());
        
        String propTitle = m.getProperty().getTitle();
        String date = m.getMeetingDate().toString();
        String time = m.getMeetingTime().toString();
        String address = m.getProperty().getAddress() + ", " + m.getProperty().getCity();
        User seller = m.getProperty().getOwner();

        if (eventType.equals("PENDING")) {
            return String.format(
                "New meeting request for %s!\nDate: %s at %s.\nCustomer Email: %s\nMessage: %s\nACTION: Please log in to your seller dashboard to Accept/Reject or Propose a new time.",
                propTitle, date, time, m.getCustomer().getEmail(), m.getCustomerMessage()
            );
        }
        
        if (eventType.equals("PROPOSED_CHANGE") && recipientRole.equals("customer")) {
            return String.format(
                "Time Change Proposed for %s.\nNew Time: %s at %s.\nSeller Note: %s\nACTION: Please review and confirm/reject the new time in your My Meetings page.",
                propTitle, date, time, m.getSellerNote()
            );
        }
        
        if (eventType.equals("SCHEDULED")) {
            // Detailed message for final confirmation
            return String.format(
                "Your meeting for the property '%s' is CONFIRMED.\n" +
                "Date: %s\n" +
                "Time: %s\n" +
                "Address: %s\n" +
                "Seller: %s (%s)\n" +
                "Please arrive promptly.",
                propTitle, date, time, address, seller.getFullName(), seller.getEmail()
            );
        }
        
        if (eventType.equals("REJECTED")) {
            return String.format("Your meeting request for %s has been rejected by the seller/customer.", propTitle);
        }

        return "Notification sent.";
    }

    private String buildBookingMessage(Booking b, String eventType) {
        // Initialize Property for safe access
        Hibernate.initialize(b.getProperty());
        
        String propTitle = b.getProperty().getTitle();
        String checkIn = b.getCheckInDate().toString();
        
        if (eventType.equals("PENDING")) {
            return String.format("You have a new booking request for %s starting %s. Please check your seller dashboard.",
                propTitle, checkIn);
        }
        
        if (eventType.equals("APPROVED")) {
            return String.format("Your booking for %s has been APPROVED! Check-in is %s.", propTitle, checkIn);
        }

        if (eventType.equals("REJECTED")) {
            return String.format("Your booking for %s has been REJECTED by the seller.", propTitle);
        }
        return "Notification sent.";
    }
}