package com.nayabas.controller;

import com.nayabas.entity.Amenity;
import com.nayabas.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityRepository amenityRepository;

    // ✅ 1. Get all amenities (public)
    @GetMapping
    public List<Amenity> getAllAmenities() {
        return amenityRepository.findAll();
    }

    // ✅ 2. Add a new amenity (SELLER or ADMIN only)
    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public Amenity addAmenity(@RequestBody Amenity amenity) {
        // Avoid duplicates
        return amenityRepository.findByNameIgnoreCase(amenity.getName())
                .orElseGet(() -> amenityRepository.save(Amenity.builder()
                        .name(amenity.getName().trim())
                        .build()));
    }
}
