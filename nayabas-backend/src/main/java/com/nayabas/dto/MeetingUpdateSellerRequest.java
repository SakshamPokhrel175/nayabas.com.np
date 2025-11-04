package com.nayabas.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat; // 💡 NEW IMPORT
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MeetingUpdateSellerRequest {
    // Note: Angular sends dates as 'yyyy-MM-dd' (ISO 8601) and time as 'HH:mm'
    
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") // 🛑 FIX
    private LocalDate newDate;
    
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm") // 🛑 FIX (Angular sends HH:mm from <input type="time">)
    private LocalTime newTime;
    
    private String sellerNote;
}