package com.nayabas.controller;

import com.nayabas.dto.PropertyRequest;
import com.nayabas.dto.PropertyResponse;
import com.nayabas.entity.PropertyImage;
import com.nayabas.entity.User;
import com.nayabas.repository.PropertyImageRepository;
import com.nayabas.repository.UserRepository;
import com.nayabas.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final UserRepository userRepository;
    private final PropertyImageRepository imageRepo; // Used for direct image fetch

    // POST: Create new property (SELLER)
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<PropertyResponse> createProperty(@RequestBody PropertyRequest request,
                                                         Authentication authentication) {
        String username = authentication.getName();
        PropertyResponse created = propertyService.createProperty(request, username);
        return ResponseEntity.ok(created);
    }

    // PUT: Update property (SELLER or ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<PropertyResponse> updateProperty(@PathVariable Long id,
                                                         @RequestBody PropertyRequest request,
                                                         Authentication authentication) {
        String username = authentication.getName();
        PropertyResponse updated = propertyService.updateProperty(id, request, username);
        return ResponseEntity.ok(updated);
    }

    // GET: Get all properties (Public)
    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    // GET: Get property by ID (Public)
    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    // GET: Search property by city (Public)
    @GetMapping("/search")
    public ResponseEntity<List<PropertyResponse>> searchByCity(@RequestParam String city) {
        return ResponseEntity.ok(propertyService.searchByCity(city));
    }

    // GET: Logged-in Seller’s properties
    @GetMapping("/my")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<PropertyResponse>> getMyProperties(Authentication authentication) {
        String username = authentication.getName();
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(propertyService.getPropertiesByOwnerId(owner.getId()));
    }

    // GET: Monthly earnings (SELLER)
    @GetMapping("/earnings/monthly")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Map<String, Double>> getMonthlyEarnings(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(propertyService.calculateMonthlyEarnings(username));
    }

    // DELETE: Delete property (SELLER or ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        propertyService.deleteProperty(id, username);
        return ResponseEntity.noContent().build();
    }

    // DELETE: Delete a single image (FIXED & CLEANED)
    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public ResponseEntity<Void> deletePropertyImage(@PathVariable Long imageId, Authentication authentication) {
        String username = authentication.getName();
        propertyService.deleteImage(imageId, username);
        return ResponseEntity.noContent().build();
    }

    // GET: Admin: Get all properties with owners
    @GetMapping("/all-with-owners")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PropertyResponse>> getAllWithOwners() {
        return ResponseEntity.ok(propertyService.getAllPropertiesWithOwners());
    }

    // GET: Default image (fallback)
    @GetMapping("/default-image")
    public ResponseEntity<byte[]> getDefaultImage() throws IOException {
        ClassPathResource imgFile = new ClassPathResource("static/default-property.jpg");
        byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bytes);
    }

    // GET: Get a property’s specific image
    @GetMapping("/{id}/images/{imageId}")
    public ResponseEntity<byte[]> getPropertyImage(@PathVariable Long id,
                                                 @PathVariable Long imageId) {
        PropertyImage image = imageRepo.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image.getImageData());
    }
}


//package com.nayabas.controller;
//
//import com.nayabas.dto.PropertyRequest;
//import com.nayabas.dto.PropertyResponse;
//import com.nayabas.entity.PropertyImage;
//import com.nayabas.entity.User;
//import com.nayabas.repository.PropertyImageRepository;
//import com.nayabas.repository.UserRepository;
//import com.nayabas.service.PropertyService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.util.StreamUtils;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.nio.file.AccessDeniedException;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/properties")
//@RequiredArgsConstructor
//public class PropertyController {
//
//    private final PropertyService propertyService;
//    private final UserRepository userRepository;
//    private final PropertyImageRepository imageRepo;
//
//    // ✅ Create new property (SELLER)
//    @PostMapping
//    @PreAuthorize("hasRole('SELLER')")
//    public ResponseEntity<PropertyResponse> createProperty(@RequestBody PropertyRequest request,
//                                                           Authentication authentication) {
//        String username = authentication.getName();
//        PropertyResponse created = propertyService.createProperty(request, username);
//        return ResponseEntity.ok(created);
//    }
//
//    // ✅ Update property (SELLER or ADMIN)
//    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
//    public ResponseEntity<PropertyResponse> updateProperty(@PathVariable Long id,
//                                                           @RequestBody PropertyRequest request,
//                                                           Authentication authentication) {
//        String username = authentication.getName();
//        PropertyResponse updated = propertyService.updateProperty(id, request, username);
//        return ResponseEntity.ok(updated);
//    }
//
//    // ✅ Get all properties (Public)
//    @GetMapping
//    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
//        return ResponseEntity.ok(propertyService.getAllProperties());
//    }
//
//    // ✅ Get property by ID (Public)
//    @GetMapping("/{id}")
//    public ResponseEntity<PropertyResponse> getPropertyById(@PathVariable Long id) {
//        return ResponseEntity.ok(propertyService.getPropertyById(id));
//    }
//
//    // ✅ Search property by city (Public)
//    @GetMapping("/search")
//    public ResponseEntity<List<PropertyResponse>> searchByCity(@RequestParam String city) {
//        return ResponseEntity.ok(propertyService.searchByCity(city));
//    }
//
//    // ✅ Logged-in Seller’s properties
//    @GetMapping("/my")
//    @PreAuthorize("hasRole('SELLER')")
//    public ResponseEntity<List<PropertyResponse>> getMyProperties(Authentication authentication) {
//        String username = authentication.getName();
//        User owner = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        return ResponseEntity.ok(propertyService.getPropertiesByOwnerId(owner.getId()));
//    }
//
//    // ✅ Monthly earnings (SELLER)
//    @GetMapping("/earnings/monthly")
//    @PreAuthorize("hasRole('SELLER')")
//    public ResponseEntity<Map<String, Double>> getMonthlyEarnings(Authentication authentication) {
//        String username = authentication.getName();
//        return ResponseEntity.ok(propertyService.calculateMonthlyEarnings(username));
//    }
//
//    // ✅ Delete property
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
//    public ResponseEntity<Void> deleteProperty(@PathVariable Long id, Authentication authentication) {
//        String username = authentication.getName();
//        propertyService.deleteProperty(id, username);
//        return ResponseEntity.noContent().build();
//    }
//
//
// // ✅ Delete a single image
// @DeleteMapping("/images/{imageId}")
// @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
// public ResponseEntity<Void> deletePropertyImage(@PathVariable Long imageId, Authentication authentication) /* REMOVE 'throws AccessDeniedException' */ {
//     String username = authentication.getName();
//     // The service handles permission check for SELLER/ADMIN now
//     propertyService.deleteImage(imageId, username);
//     return ResponseEntity.noContent().build();
// }
//
//    // ✅ Admin: Get all properties with owners
//    @GetMapping("/all-with-owners")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<PropertyResponse>> getAllWithOwners() {
//        return ResponseEntity.ok(propertyService.getAllPropertiesWithOwners());
//    }
//
//    // ✅ Default image (fallback)
//    @GetMapping("/default-image")
//    public ResponseEntity<byte[]> getDefaultImage() throws IOException {
//        ClassPathResource imgFile = new ClassPathResource("static/default-property.jpg");
//        byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG)
//                .body(bytes);
//    }
//
//    // ✅ Get a property’s specific image
//    @GetMapping("/{id}/images/{imageId}")
//    public ResponseEntity<byte[]> getPropertyImage(@PathVariable Long id,
//                                                   @PathVariable Long imageId) {
//        PropertyImage image = imageRepo.findById(imageId)
//                .orElseThrow(() -> new RuntimeException("Image not found"));
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG)
//                .body(image.getImageData());
//    }
//}

