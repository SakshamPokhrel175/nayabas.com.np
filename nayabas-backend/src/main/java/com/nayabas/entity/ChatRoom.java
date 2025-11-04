package com.nayabas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The unique ID used for the WebSocket channel (e.g., /topic/chat/UUID)
    @Column(unique = true, nullable = false)
    private String roomId; 
    
    // Links to the meeting that enabled this chat
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", unique = true, nullable = false)
    private Meeting meeting;
    
    // Status: ACTIVE (chat is live) or DESTROYED (chat link should be disabled)
    @Enumerated(EnumType.STRING)
    private ChatStatus status; 

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime destroyedAt;

    public enum ChatStatus {
        ACTIVE,
        DESTROYED
    }
}