package com.nayabas.service;

import com.nayabas.entity.ChatRoom;
import com.nayabas.entity.Meeting;
import com.nayabas.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ChatRoomRepository chatRoomRepository; 

    /**
     * Creates a new chat room linked to a SCHEDULED meeting.
     * Called only when a meeting status is SCHEDULED.
     */
    public ChatRoom createRoomForMeeting(Meeting meeting) {
        if (meeting.getMeetingStatus() != Meeting.Status.SCHEDULED) {
            throw new RuntimeException("Cannot create a chat room for an unscheduled meeting.");
        }
        
        // Ensure a room doesn't already exist for this meeting
        if (chatRoomRepository.findByMeetingId(meeting.getId()).isPresent()) {
             System.err.println("ChatService: Room already exists for Meeting ID " + meeting.getId());
             return chatRoomRepository.findByMeetingId(meeting.getId()).get(); 
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .meeting(meeting)
                .roomId(UUID.randomUUID().toString()) // Generates unique identifier for WebSocket topic
                .status(ChatRoom.ChatStatus.ACTIVE)
                .build();
        
        return chatRoomRepository.save(chatRoom);
    }
    
    /**
     * Marks the chat room as DESTROYED and performs participant authorization.
     */
    public void destroyRoom(String roomId, String username) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found."));
        
        // IMPORTANT: Ensure the Meeting is initialized before accessing its users
        Meeting meeting = room.getMeeting();
        
        // 🛑 SECURITY CHECK: Ensure the user ending the chat is a participant
        boolean isCustomer = meeting.getCustomer().getUsername().equals(username);
        boolean isSeller = meeting.getProperty().getOwner().getUsername().equals(username);
        
        if (!isCustomer && !isSeller) {
            throw new RuntimeException("Access Denied: You are not a participant in this chat session.");
        }
        
        room.setStatus(ChatRoom.ChatStatus.DESTROYED);
        room.setDestroyedAt(LocalDateTime.now());
        chatRoomRepository.save(room);
        
        // 💡 To be implemented: WebSocket message to disconnect active chat clients
    }
    
    // Getter for the frontend to check if a link should be active
    public ChatRoom getActiveRoomByMeetingId(Long meetingId) {
        return chatRoomRepository.findByMeetingIdAndStatus(
                meetingId, ChatRoom.ChatStatus.ACTIVE)
                .orElse(null);
    }

    /**
     * Finds the ChatRoom entity by its unique ID (roomId).
     * Used by ChatController/MeetingService to get the associated Meeting.
     */
	public Optional<ChatRoom> findByRoomId(String roomId) {
		// 🛑 FIX: Correctly uses the repository method.
		return chatRoomRepository.findByRoomId(roomId); 
	}
}
