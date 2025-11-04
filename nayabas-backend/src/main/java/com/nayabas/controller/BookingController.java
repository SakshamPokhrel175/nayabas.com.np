package com.nayabas.controller;

import com.nayabas.dto.BookingRequest;
import com.nayabas.entity.Booking;
import com.nayabas.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /** Create booking by customer */
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public Booking book(@RequestBody BookingRequest request, Principal principal) {
        // NOTE: The notification call will be inside the service layer.
        return bookingService.createBooking(request, principal.getName());
    }

    /** Seller updates booking status (approve / reject) */
    @PreAuthorize("hasRole('SELLER')")
    @PutMapping("/{bookingId}/status")
    public Booking updateStatus(@PathVariable Long bookingId, @RequestParam String status) {
        // NOTE: The notification call will be inside the service layer.
        return bookingService.updateStatus(bookingId, status);
    }

    /** Customer can view their own bookings */
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/me")
    public List<Booking> myBookings(Principal principal) {
        return bookingService.getBookingsByCustomer(principal.getName());
    }

    /** Seller can view bookings for their own properties */
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller")
    public List<Booking> sellerBookings(Principal principal) {
        return bookingService.getBookingsBySeller(principal.getName());
    }
}