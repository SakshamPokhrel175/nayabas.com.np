package com.nayabas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate meetingDate;
    private LocalTime meetingTime;

    @Column(length = 1000)
    private String customerMessage;

    @Column(length = 1000) // 💡 NEW: Seller's note when proposing a change
    private String sellerNote;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_status", length = 20) 
    private Status meetingStatus;

    public enum Status {
        PENDING,
        SCHEDULED,
        REJECTED,
        PROPOSED_CHANGE, // 💡 NEW STATUS
        CHAT_COMPLETED, // 🛑 NEW STATUS: Chat is over, ready for follow-up actions
        CLOSED // Optional: Final end state
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;
    
    // 🛑 NEW FIELD: Link to the Chat Room (One-to-One is ideal here)
    @OneToOne(mappedBy = "meeting", fetch = FetchType.LAZY) 
    private ChatRoom chatRoom;
}


