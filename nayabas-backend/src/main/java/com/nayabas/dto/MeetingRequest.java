package com.nayabas.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class MeetingRequest {
    private Long propertyId;
    private LocalDate meetingDate;
    private LocalTime meetingTime;
    private String customerMessage;
}