package com.nayabas.repository;

import com.nayabas.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPropertyId(Long propertyId);
    List<Review> findByCustomerId(Long customerId);
}