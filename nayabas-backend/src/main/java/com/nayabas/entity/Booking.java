package com.nayabas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    public enum Status { PENDING, CONFIRMED, REJECTED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Property property;

    @ManyToOne
    private User customer;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String customerContact;

    @Enumerated(EnumType.STRING)
    private Status bookingStatus;
}