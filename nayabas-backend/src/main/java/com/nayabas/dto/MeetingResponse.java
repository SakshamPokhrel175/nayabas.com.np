package com.nayabas.dto;

import com.nayabas.entity.Property;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // 🛑 CRUCIAL IMPORT
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
//🛑 FIX: Tell Jackson to ignore Hibernate's internal proxy fields
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
public class MeetingResponse {
    private Long id;
    private Property property; // includes title, location, owner, etc.
    private LocalDate meetingDate;
    private LocalTime meetingTime;
    private String customerMessage;
    private String sellerNote; // 💡 NEW FIELD
    private String meetingStatus;
    private CustomerResponse customer;
    private String chatRoomId; // 🛑 NEW FIELD
}
