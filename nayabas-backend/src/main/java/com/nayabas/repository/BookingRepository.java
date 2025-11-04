package com.nayabas.repository;

import com.nayabas.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerUsername(String username);
    List<Booking> findByPropertyId(Long propertyId);

    // New: find bookings for seller’s properties
    List<Booking> findByPropertyOwnerUsername(String ownerUsername);
}