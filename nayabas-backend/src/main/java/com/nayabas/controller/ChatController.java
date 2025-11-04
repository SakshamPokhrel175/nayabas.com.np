package com.nayabas.controller;

import com.nayabas.service.ChatService;
import com.nayabas.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MeetingService meetingService;

    /**
     * Endpoint for Customer or Seller to end the chat session.
     * This marks the ChatRoom as DESTROYED and updates the Meeting status to CHAT_COMPLETED.
     */
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER')")
    @PostMapping("/{roomId}/end")
    public ResponseEntity<?> endChat(@PathVariable String roomId, Principal principal) {
        
        // 1. Destroy the chat room (ChatService handles security/existence check)
        // This marks the ChatRoom as DESTROYED in the DB.
        chatService.destroyRoom(roomId, principal.getName()); 
        
        // 2. Update Meeting status to CHAT_COMPLETED
        // This is crucial for showing the "Book Now!" button on the dashboard list.
        meetingService.setMeetingStatusToCompleted(roomId); 

        // 3. Return success response
        return ResponseEntity.ok(Map.of("message", "Chat session ended. The meeting status has been updated."));
    }
    
    // 💡 Optional: Add GET /api/chat/{roomId}/status to check if the room is active
    // This is useful for the Angular component to know if the chat should be disabled.
}