package com.nayabas.service;

import com.nayabas.dto.BookingRequest;
import com.nayabas.entity.Booking;
import com.nayabas.entity.Property;
import com.nayabas.entity.User;
import com.nayabas.repository.BookingRepository;
import com.nayabas.repository.PropertyRepository;
import com.nayabas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final NotificationService notificationService; // 🛑 INJECTED SERVICE

    /** Customer creates booking */
    public Booking createBooking(BookingRequest request, String username) {
        User customer = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        Booking booking = Booking.builder()
                .property(property)
                .customer(customer)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .customerContact(request.getCustomerContact())
                .bookingStatus(Booking.Status.PENDING)
                .build();

        Booking saved = bookingRepository.save(booking);

        // 🔔 INTEGRATION: Notify Seller about new PENDING booking
        notificationService.sendBookingUpdateNotification(saved, "PENDING");
        
        return saved;
    }

    /** Seller updates booking status */
    public Booking updateStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setBookingStatus(Booking.Status.valueOf(status));
        Booking updated = bookingRepository.save(booking);

        // 🔔 INTEGRATION: Notify Customer about APPROVED/REJECTED status
        notificationService.sendBookingUpdateNotification(updated, status);
        
        return updated;
    }

    /** Get bookings for logged-in customer */
    public List<Booking> getBookingsByCustomer(String username) {
        return bookingRepository.findByCustomerUsername(username);
    }

    /** Get bookings for seller's properties */
    public List<Booking> getBookingsBySeller(String sellerUsername) {
        return bookingRepository.findByPropertyOwnerUsername(sellerUsername);
    }
}


//package com.nayabas.service;
//
//import com.nayabas.dto.BookingRequest;
//import com.nayabas.entity.Booking;
//import com.nayabas.entity.Property;
//import com.nayabas.entity.User;
//import com.nayabas.repository.BookingRepository;
//import com.nayabas.repository.PropertyRepository;
//import com.nayabas.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class BookingService {
//
//    private final BookingRepository bookingRepository;
//    private final UserRepository userRepository;
//    private final PropertyRepository propertyRepository;
//    private final NotificationService notificationService; // 💡 NEW: Inject NotificationService
//
//
//    /** Customer creates booking */
//    public Booking createBooking(BookingRequest request, String username) {
//        User customer = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Property property = propertyRepository.findById(request.getPropertyId())
//                .orElseThrow(() -> new RuntimeException("Property not found"));
//
//        Booking booking = Booking.builder()
//                .property(property)
//                .customer(customer)
//                .checkInDate(request.getCheckInDate())
//                .checkOutDate(request.getCheckOutDate())
//                .customerContact(request.getCustomerContact())
//                .bookingStatus(Booking.Status.PENDING)
//                .build();
//
//        return bookingRepository.save(booking);
//    }
//
//    /** Seller updates booking status */
//    public Booking updateStatus(Long bookingId, String status) {
//        Booking booking = bookingRepository.findById(bookingId)
//                .orElseThrow(() -> new RuntimeException("Booking not found"));
//        booking.setBookingStatus(Booking.Status.valueOf(status));
//        return bookingRepository.save(booking);
//    }
//
//    /** Get bookings for logged-in customer */
//    public List<Booking> getBookingsByCustomer(String username) {
//        return bookingRepository.findByCustomerUsername(username);
//    }
//
//    /** Get bookings for seller's properties */
//    public List<Booking> getBookingsBySeller(String sellerUsername) {
//        return bookingRepository.findByPropertyOwnerUsername(sellerUsername);
//    }
//}
