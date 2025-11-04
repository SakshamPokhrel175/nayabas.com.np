package com.nayabas.service;

import com.nayabas.entity.*;
import com.nayabas.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public Review create(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByProperty(Long propertyId) {
        return reviewRepository.findByPropertyId(propertyId);
    }
}