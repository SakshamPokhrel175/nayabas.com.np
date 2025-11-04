package com.nayabas.controller;

import com.nayabas.entity.Review;
import com.nayabas.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public Review create(@RequestBody Review review) {
        return reviewService.create(review);
    }

    @GetMapping("/property/{propertyId}")
    public List<Review> getByProperty(@PathVariable Long propertyId) {
        return reviewService.getReviewsByProperty(propertyId);
    }
}