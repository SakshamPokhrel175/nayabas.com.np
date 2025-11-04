package com.nayabas.service;

import com.nayabas.dto.CustomerResponse;
import com.nayabas.dto.MeetingRequest;
import com.nayabas.dto.MeetingResponse;
import com.nayabas.entity.ChatRoom;
import com.nayabas.entity.Meeting;
import com.nayabas.entity.Property;
import com.nayabas.entity.User;
import com.nayabas.repository.MeetingRepository;
import com.nayabas.repository.PropertyRepository;
import com.nayabas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.hibernate.Hibernate; // 🛑 CRUCIAL IMPORT for initialization
import org.springframework.transaction.annotation.Transactional; // 🛑 IMPORTANT: for schema update/retrieval


import java.time.LocalDate; // 💡 NEW IMPORT
import java.time.LocalTime; // 💡 NEW IMPORT
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

	 private final MeetingRepository meetingRepository;
	    private final UserRepository userRepository;
	    private final PropertyRepository propertyRepository;
	    private final SimpMessagingTemplate messagingTemplate; 
	    private final NotificationService notificationService;
	    private final ChatService chatService; // 🛑 INJECT THE CHAT SERVICE
	    
	    // 🏆 NEW HELPER: Fetches the meeting and initializes the ChatRoom link
	    private Meeting getFullyInitializedMeeting(Long meetingId) {
	        Meeting meeting = meetingRepository.findById(meetingId)
	            .orElseThrow(() -> new RuntimeException("Meeting not found for serialization."));
	            
	        // Manually initialize the ChatRoom link
	        if (meeting.getChatRoom() != null) {
	            Hibernate.initialize(meeting.getChatRoom());
	            // This is crucial: access the field to ensure the proxy is gone
	            meeting.getChatRoom().getRoomId(); 
	        }
	        
	        // Initialize other entities as before (Property, Customer)
	        Hibernate.initialize(meeting.getProperty());
	        Hibernate.initialize(meeting.getProperty().getOwner());
	        Hibernate.initialize(meeting.getProperty().getAmenities());
	        Hibernate.initialize(meeting.getProperty().getImages());
	        Hibernate.initialize(meeting.getCustomer());
	        
	        return meeting;
	    }

	    /** ✅ Create a new meeting request (Customer) */
	    public Meeting createMeeting(MeetingRequest request, String username) {
	        User customer = userRepository.findByUsername(username)
	                .orElseThrow(() -> new RuntimeException("User not found"));
	        Property property = propertyRepository.findById(request.getPropertyId())
	                .orElseThrow(() -> new RuntimeException("Property not found"));

	        Meeting meeting = Meeting.builder()
	                .property(property)
	                .customer(customer)
	                .meetingDate(request.getMeetingDate())
	                .meetingTime(request.getMeetingTime())
	                .customerMessage(request.getCustomerMessage())
	                .meetingStatus(Meeting.Status.PENDING)
	                .build();

	        Meeting saved = meetingRepository.save(meeting);
	        
	        // 🔔 INTEGRATION: Notify Seller about new PENDING request
	        notificationService.sendMeetingUpdateNotification(saved, "PENDING"); 

	        // ✅ Broadcast to all subscribed clients (sellers, admins, etc.)
	        messagingTemplate.convertAndSend("/topic/meetings", mapToResponseForSeller(saved));

	        return saved;
	    }

	    /** ✅ Seller updates meeting status (Initial Accept/Reject) */
	    public Meeting updateStatus(Long meetingId, String status) {
	        Meeting meeting = meetingRepository.findById(meetingId)
	                .orElseThrow(() -> new RuntimeException("Meeting not found"));

	        meeting.setMeetingStatus(Meeting.Status.valueOf(status));
	        Meeting updated = meetingRepository.save(meeting);
	        
	        if (updated.getMeetingStatus() == Meeting.Status.SCHEDULED) {
	            // 🛑 INTEGRATION: CREATE CHAT ROOM if Seller directly accepts PENDING
	            chatService.createRoomForMeeting(updated); 
	        }
	        
	        // 🏆 FIX: Get fresh, fully initialized object for serialization and notification
	        Meeting meetingForSerialization = getFullyInitializedMeeting(meetingId);
	        
	        notificationService.sendMeetingUpdateNotification(meetingForSerialization, status);
	        messagingTemplate.convertAndSend("/topic/meetings", mapToResponseForSeller(meetingForSerialization));

	        return updated;
	    }

	    /** 💡 NEW METHOD: Seller proposes a new date/time */
	    public MeetingResponse proposeChange(Long meetingId, LocalDate newDate, LocalTime newTime, String sellerNote, String sellerUsername) {
	        Meeting meeting = meetingRepository.findById(meetingId)
	                .orElseThrow(() -> new RuntimeException("Meeting not found."));

	        // 🔒 Security Check: Must be the property owner
	        if (!meeting.getProperty().getOwner().getUsername().equals(sellerUsername)) {
	            throw new RuntimeException("Access Denied: You do not own this property.");
	        }

	        // ⚠️ Logic: Can only propose changes to PENDING requests
	        if (meeting.getMeetingStatus() != Meeting.Status.PENDING) {
	            throw new RuntimeException("Can only propose changes to PENDING requests.");
	        }

	        // 1. Update fields and status
	        meeting.setMeetingDate(newDate);
	        meeting.setMeetingTime(newTime);
	        meeting.setSellerNote(sellerNote);
	        meeting.setMeetingStatus(Meeting.Status.PROPOSED_CHANGE);

	        Meeting updatedMeeting = meetingRepository.save(meeting);
	        notificationService.sendMeetingUpdateNotification(updatedMeeting, "PROPOSED_CHANGE");
	        messagingTemplate.convertAndSend("/topic/meetings", mapToResponseForSeller(updatedMeeting));
	        return mapToResponseForSeller(updatedMeeting);
	    }

	    /** 💡 NEW METHOD: Customer accepts the proposed change (Final confirmation) */
	    public MeetingResponse confirmProposedChange(Long meetingId, String customerUsername) {
	        Meeting meeting = meetingRepository.findById(meetingId)
	                .orElseThrow(() -> new RuntimeException("Meeting not found."));

	        // 🔒 Security Check: Must be the customer who made the request
	        if (!meeting.getCustomer().getUsername().equals(customerUsername)) {
	            throw new RuntimeException("Access Denied: You are not the customer for this meeting.");
	        }

	        if (meeting.getMeetingStatus() != Meeting.Status.PROPOSED_CHANGE) {
	            throw new RuntimeException("Meeting status must be PROPOSED_CHANGE to confirm.");
	        }

	        // Finalize the meeting
	        meeting.setMeetingStatus(Meeting.Status.SCHEDULED);

	        Meeting updatedMeeting = meetingRepository.save(meeting);
	        
	        // 🛑 INTEGRATION: CREATE CHAT ROOM
	        chatService.createRoomForMeeting(updatedMeeting); 

	        // 🏆 FIX: Get fresh, fully initialized object for serialization and notification
	        Meeting meetingForSerialization = getFullyInitializedMeeting(meetingId);

	        notificationService.sendMeetingUpdateNotification(meetingForSerialization, "SCHEDULED"); 
	        messagingTemplate.convertAndSend("/topic/meetings", mapToResponseForSeller(meetingForSerialization));

	        // Use the fresh object for the final DTO return
	        return mapToResponseForCustomer(meetingForSerialization); 
	    }

	    /**
	     * 💡 NEW METHOD: Called by ChatController to finalize meeting status after chat ends.
	     */
	    @Transactional
	    public void setMeetingStatusToCompleted(String roomId) {
	        ChatRoom room = chatService.findByRoomId(roomId) // Assumes ChatService has a findByRoomId getter
	                                  .orElseThrow(() -> new RuntimeException("Chat room not found."));

	        Meeting meeting = room.getMeeting();
	        
	        if (meeting.getMeetingStatus() != Meeting.Status.SCHEDULED) {
	            // Already completed or rejected, do nothing major
	            System.err.println("Meeting status was already non-SCHEDULED. Status: " + meeting.getMeetingStatus());
	        }

	        meeting.setMeetingStatus(Meeting.Status.CHAT_COMPLETED);
	        Meeting updatedMeeting = meetingRepository.save(meeting);
	        
	        // 🔔 Notify participants via WebSocket that the chat phase is over
	        Meeting meetingForSerialization = getFullyInitializedMeeting(meeting.getId());
	        
	        // Send notification about the status change
	        notificationService.sendMeetingUpdateNotification(meetingForSerialization, "CHAT_COMPLETED");
	        
	        // Broadcast final status update (this updates the dashboard list)
	        messagingTemplate.convertAndSend("/topic/meetings", mapToResponseForSeller(meetingForSerialization));
	    }
	    

//	    /** ✅ Get all meetings for a customer */
//	    public List<MeetingResponse> getMeetingsByCustomerResponse(String username) {
//	        List<Meeting> meetings = meetingRepository.findByCustomerUsername(username);
//	        return meetings.stream()
//	                .map(m -> getFullyInitializedMeeting(m.getId())) // Fetch fresh, linked data for each meeting
//	                .map(this::mapToResponseForCustomer).toList();
//	    }
//
//	    /** ✅ Get all meetings for a seller */
//	    public List<MeetingResponse> getMeetingsBySeller(String sellerUsername) {
//	        List<Meeting> meetings = meetingRepository.findByPropertyOwnerUsername(sellerUsername);
//	        return meetings.stream()
//	                .map(m -> getFullyInitializedMeeting(m.getId()))
//	                .map(this::mapToResponseForSeller).toList();
//	    }
	    
	    public List<MeetingResponse> getMeetingsByCustomerResponse(String username) {
	        // 🛑 USE NEW FETCH JOIN QUERY
	        List<Meeting> meetings = meetingRepository.findMeetingsWithChatRoomByCustomer(username); 
	        
	        // The previous complex mapper logic should still run to ensure DTO conversion is safe
	        return meetings.stream()
	                .map(this::mapToResponseForCustomer).toList(); // No need for getFullyInitializedMeeting here
	    }

	    /** ✅ Get all meetings for a seller */
	    // Remove @Transactional if present
	    public List<MeetingResponse> getMeetingsBySeller(String sellerUsername) {
	        // 🛑 USE NEW FETCH JOIN QUERY
	        List<Meeting> meetings = meetingRepository.findMeetingsWithChatRoomBySeller(sellerUsername);
	        
	        // The previous complex mapper logic should still run to ensure DTO conversion is safe
	        return meetings.stream()
	                .map(this::mapToResponseForSeller).toList(); // No need for getFullyInitializedMeeting here
	    }

    // ... (rest of mappers and helper methods remain unchanged)

    
    // ----- 🧩 Helper Mapping (Final Deep Initialization) -----

    private MeetingResponse mapToResponseForSeller(Meeting m) {
        MeetingResponse dto = new MeetingResponse();

        // 🛑 FIX: Force Deep Initialization for EAGER/PROXIED Entities
        if (m.getProperty() != null) {
            // Initialize the main Property entity proxy
            Hibernate.initialize(m.getProperty());
            
            // Initialize nested EAGER relations (Owner, Amenities, Images)
            Hibernate.initialize(m.getProperty().getOwner());
            Hibernate.initialize(m.getProperty().getAmenities()); 
            Hibernate.initialize(m.getProperty().getImages()); 
            
            // Access a simple field to ensure proxy is resolved
            m.getProperty().getTitle(); 
        }
        
        // 🛑 FIX: MAP THE CHAT ROOM ID
        if (m.getChatRoom() != null) {
            // Initialize the proxy, though it should be available via Hibernate.initialize
            Hibernate.initialize(m.getChatRoom());
            dto.setChatRoomId(m.getChatRoom().getRoomId());
        }

        // 🛑 FIX: Force Initialization for LAZY Customer Entity
        User c = m.getCustomer(); // This fetch is LAZY
        if (c != null) {
            c.getFullName(); // Force initialization
            
            CustomerResponse cust = new CustomerResponse();
            cust.setId(c.getId());
            cust.setUsername(c.getUsername());
            cust.setFullName(c.getFullName());
            cust.setEmail(c.getEmail());
            cust.setPhoneNumber(c.getPhoneNumber());
            cust.setAddress(c.getAddressLine1());
            cust.setProfileImage(c.getProfileImage()); 
            
            dto.setCustomer(cust);
        }

        dto.setId(m.getId());
        dto.setProperty(m.getProperty()); // This should now be fully initialized
        dto.setMeetingDate(m.getMeetingDate());
        dto.setMeetingTime(m.getMeetingTime());
        dto.setCustomerMessage(m.getCustomerMessage());
        dto.setMeetingStatus(m.getMeetingStatus().name());
        dto.setSellerNote(m.getSellerNote());

        return dto;
    }

    private MeetingResponse mapToResponseForCustomer(Meeting m) {
        MeetingResponse dto = new MeetingResponse();

        // 🛑 FIX: Force Deep Initialization for EAGER/PROXIED Entities
        if (m.getProperty() != null) {
            Hibernate.initialize(m.getProperty());
            Hibernate.initialize(m.getProperty().getOwner());
            Hibernate.initialize(m.getProperty().getAmenities());
            Hibernate.initialize(m.getProperty().getImages());
            m.getProperty().getTitle();
        }

        dto.setId(m.getId());
        dto.setProperty(m.getProperty()); // This should now be fully initialized
        dto.setMeetingDate(m.getMeetingDate());
        dto.setMeetingTime(m.getMeetingTime());
        dto.setCustomerMessage(m.getCustomerMessage());
        dto.setMeetingStatus(m.getMeetingStatus().name());
        dto.setSellerNote(m.getSellerNote());

        return dto;
    }
}
