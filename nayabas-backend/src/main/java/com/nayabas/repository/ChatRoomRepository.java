package com.nayabas.repository;

import com.nayabas.entity.ChatRoom;
import com.nayabas.entity.ChatRoom.ChatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByMeetingId(Long meetingId);
    Optional<ChatRoom> findByRoomId(String roomId);
    Optional<ChatRoom> findByMeetingIdAndStatus(Long meetingId, ChatStatus status);
}