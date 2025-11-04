package com.nayabas.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {
    private Long propertyId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String customerContact;
}