package com.nayabas.repository;

import com.nayabas.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByCustomerUsername(String username);
    List<Meeting> findByPropertyOwnerUsername(String username);
    // 🛑 FIX: Use Fetch Join to ensure ChatRoom is loaded immediately
    @Query("SELECT m FROM Meeting m LEFT JOIN FETCH m.chatRoom WHERE m.customer.username = :username")
    List<Meeting> findMeetingsWithChatRoomByCustomer(@Param("username") String username);

    @Query("SELECT m FROM Meeting m LEFT JOIN FETCH m.chatRoom WHERE m.property.owner.username = :username")
    List<Meeting> findMeetingsWithChatRoomBySeller(@Param("username") String username);

}