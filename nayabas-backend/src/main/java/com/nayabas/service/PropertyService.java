package com.nayabas.service;

import com.nayabas.dto.OwnerResponse;
import com.nayabas.dto.PropertyImageResponse;
import com.nayabas.dto.PropertyRequest;
import com.nayabas.dto.PropertyResponse;
import com.nayabas.entity.Amenity;
import com.nayabas.entity.Property;
import com.nayabas.entity.PropertyImage;
import com.nayabas.entity.User;
import com.nayabas.repository.AmenityRepository;
import com.nayabas.repository.PropertyImageRepository;
import com.nayabas.repository.PropertyRepository;
import com.nayabas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.*;
import java.util.Base64;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final PropertyImageRepository propertyImageRepository;

    // ============================================================
    // CRUD Operations
    // ============================================================

    // ✅ Create property
    public PropertyResponse createProperty(PropertyRequest req, String sellerUsername) {
        User owner = userRepository.findByUsername(sellerUsername)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        Property property = buildPropertyFromRequest(req, owner);
        Property saved = propertyRepository.save(property);
        return mapToResponse(saved);
    }

    // ✅ Update property
    public PropertyResponse updateProperty(Long id, PropertyRequest req, String username) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔒 Only the owner (SELLER) or ADMIN can update
        if (user.getRole() == User.Role.SELLER &&
                !property.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("You can only update your own property");
        }

        // Update fields
        property.setTitle(req.getTitle());
        property.setDescription(req.getDescription());
        property.setAddress(req.getAddress());
        property.setHouseNumber(req.getHouseNumber());
        property.setCity(req.getCity());
        property.setDistrict(req.getDistrict());
        property.setPrice(req.getPrice());
        property.setBedrooms(req.getBedrooms());
        property.setLatitude(req.getLatitude());
        property.setLongitude(req.getLongitude());

        // Update amenities
        if (req.getAmenityIds() != null) {
            Set<Amenity> amenities = new HashSet<>(amenityRepository.findAllById(req.getAmenityIds()));
            property.setAmenities(amenities);
        }

        // Add new images (existing remain)
        if (req.getImages() != null && !req.getImages().isEmpty()) {
            List<PropertyImage> newImages = req.getImages().stream()
                    .map(img -> {
                        PropertyImage image = new PropertyImage();
                        image.setProperty(property);
                        image.setImageData(Base64.getDecoder().decode(img));
                        return image;
                    }).toList();
            property.getImages().addAll(newImages);
        }

        Property updated = propertyRepository.save(property);
        return mapToResponse(updated);
    }

    // ✅ Delete property
    public void deleteProperty(Long id, String username) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔒 Only seller's own property or admin
        if (user.getRole() == User.Role.SELLER &&
                !property.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("You can only delete your own property");
        }

        // Admin can delete any property
        propertyRepository.delete(property);
    }

    // ✅ DELETE SINGLE IMAGE (FIXED implementation)
    public void deleteImage(Long imageId, String username) {
        // 1. Fetch the PropertyImage entity
        PropertyImage image = propertyImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));

        // 2. Fetch the User entity for role/owner check
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        Property property = image.getProperty(); // Get the parent entity

        // 3. Authorization Check
        String ownerUsername = property.getOwner().getUsername();
        boolean isOwner = username.equals(ownerUsername);
        boolean isAdmin = user.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Access denied: You can only delete your own property images (or need Admin privileges).");
        }

        // 4. CRITICAL JPA FIX: Remove the image from the parent entity's collection.
        // This triggers the database DELETE via orphanRemoval=true when the transaction commits.
        if (property.getImages() != null) {
            property.getImages().remove(image);
        }

        // Force a flush to commit the deletion before the HTTP response is sent,
        // preventing the Angular client from fetching stale data immediately.
        propertyImageRepository.flush();
    }


    // ============================================================
    // Retrieval Operations
    // ============================================================

    // ✅ Get all properties
    public List<PropertyResponse> getAllProperties() {
        List<Property> properties = propertyRepository.findAll();
        // The debug prints are removed for clean code, as per request.
        return properties.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ✅ Get property by ID
    public PropertyResponse getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        return mapToResponse(property);
    }

    // ✅ Search property by city
    public List<PropertyResponse> searchByCity(String city) {
        return propertyRepository.findByCityIgnoreCase(city).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ✅ Get properties by Owner ID
    public List<PropertyResponse> getPropertiesByOwnerId(Long ownerId) {
        return propertyRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ✅ ADMIN: All properties with owners
    public List<PropertyResponse> getAllPropertiesWithOwners() {
        return propertyRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ✅ Monthly earnings
    public Map<String, Double> calculateMonthlyEarnings(String username) {
        Map<String, Double> earnings = new LinkedHashMap<>();
        List<Object[]> result = propertyRepository.findMonthlyEarningsByOwner(username);

        for (Object[] row : result) {
            Integer monthNumber = (Integer) row[0];
            Double total = (Double) row[1];
            String monthName = Month.of(monthNumber).name();
            earnings.put(monthName, total);
        }
        return earnings;
    }

    // ============================================================
    // Internal Helper Methods
    // ============================================================

    // Helper for creation
    private Property buildPropertyFromRequest(PropertyRequest req, User owner) {
        Set<Amenity> amenities = new HashSet<>();
        if (req.getAmenityIds() != null && !req.getAmenityIds().isEmpty()) {
            amenities.addAll(amenityRepository.findAllById(req.getAmenityIds()));
        }

        Property property = Property.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .address(req.getAddress())
                .houseNumber(req.getHouseNumber())
                .city(req.getCity())
                .district(req.getDistrict())
                .price(req.getPrice())
                .bedrooms(req.getBedrooms())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .owner(owner)
                .amenities(amenities)
                .build();

        if (req.getImages() != null && !req.getImages().isEmpty()) {
            List<PropertyImage> images = req.getImages().stream()
                    .filter(Objects::nonNull)
                    .map(imgStr -> {
                        PropertyImage image = new PropertyImage();
                        image.setProperty(property);
                        image.setImageData(Base64.getDecoder().decode(imgStr));
                        return image;
                    }).collect(Collectors.toList());
            property.setImages(images);
        } else {
            property.setImages(new ArrayList<>());
        }

        return property;
    }

    // Mapper: ENTITY → RESPONSE DTO
    private PropertyResponse mapToResponse(Property p) {
        PropertyResponse dto = new PropertyResponse();
        dto.setId(p.getId());
        dto.setTitle(p.getTitle());
        dto.setDescription(p.getDescription());
        dto.setAddress(p.getAddress());
        dto.setHouseNumber(p.getHouseNumber());
        dto.setCity(p.getCity());
        dto.setDistrict(p.getDistrict());
        dto.setPrice(p.getPrice());
        dto.setBedrooms(p.getBedrooms());
        dto.setLatitude(p.getLatitude());
        dto.setLongitude(p.getLongitude());
        dto.setAmenities(p.getAmenities());
        
        // Map images
        if (p.getImages() != null) {
            dto.setImages(p.getImages().stream()
                          .map(PropertyImageResponse::new)
                          .toList());
        }

        // Map owner
        if (p.getOwner() != null) {
            OwnerResponse owner = new OwnerResponse();
            owner.setId(p.getOwner().getId());
            owner.setUsername(p.getOwner().getUsername());
            owner.setEmail(p.getOwner().getEmail());
            owner.setFullName(p.getOwner().getFullName());
            owner.setRole(p.getOwner().getRole().name());
            dto.setOwner(owner);
        }
        return dto;
    }
}


//package com.nayabas.service;
//
//import com.nayabas.dto.OwnerResponse;
//import com.nayabas.dto.PropertyImageResponse;
//import com.nayabas.dto.PropertyRequest;
//import com.nayabas.dto.PropertyResponse;
//import com.nayabas.entity.Amenity;
//import com.nayabas.entity.Property;
//import com.nayabas.entity.PropertyImage;
//import com.nayabas.entity.User;
//import com.nayabas.repository.AmenityRepository;
//import com.nayabas.repository.PropertyImageRepository;
//import com.nayabas.repository.PropertyRepository;
//import com.nayabas.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.nio.file.AccessDeniedException;
//import java.time.Month;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.Base64;
//
//@Service
//@RequiredArgsConstructor
//public class PropertyService {
//
//    private final PropertyRepository propertyRepository;
//    private final UserRepository userRepository;
//    private final AmenityRepository amenityRepository;
//    private final PropertyImageRepository propertyImageRepository;
//
//
//    // ✅ Create property
//    public PropertyResponse createProperty(PropertyRequest req, String sellerUsername) {
//        User owner = userRepository.findByUsername(sellerUsername)
//                .orElseThrow(() -> new RuntimeException("Seller not found"));
//
//        Property property = buildPropertyFromRequest(req, owner);
//        Property saved = propertyRepository.save(property);
//        return mapToResponse(saved);
//    }
//
//    // ✅ Update property
//    public PropertyResponse updateProperty(Long id, PropertyRequest req, String username) {
//        Property property = propertyRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Property not found"));
//
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // 🔒 Only the owner (SELLER) or ADMIN can update
//        if (user.getRole() == User.Role.SELLER &&
//                !property.getOwner().getUsername().equals(username)) {
//            throw new RuntimeException("You can only update your own property");
//        }
//
//        // ✅ Update fields
//        property.setTitle(req.getTitle());
//        property.setDescription(req.getDescription());
//        property.setAddress(req.getAddress());
//        property.setHouseNumber(req.getHouseNumber());
//        property.setCity(req.getCity());
//        property.setDistrict(req.getDistrict());
//        property.setPrice(req.getPrice());
//        property.setBedrooms(req.getBedrooms());
//        property.setLatitude(req.getLatitude());
//        property.setLongitude(req.getLongitude());
//
//        // ✅ Update amenities
//        if (req.getAmenityIds() != null) {
//            Set<Amenity> amenities = new HashSet<>(amenityRepository.findAllById(req.getAmenityIds()));
//            property.setAmenities(amenities);
//        }
//
//        // ✅ Add new images (existing remain)
//        if (req.getImages() != null && !req.getImages().isEmpty()) {
//            List<PropertyImage> newImages = req.getImages().stream()
//                    .map(img -> {
//                        PropertyImage image = new PropertyImage();
//                        image.setProperty(property);
//                        image.setImageData(Base64.getDecoder().decode(img));
//                        return image;
//                    }).toList();
//            property.getImages().addAll(newImages);
//        }
//
//        Property updated = propertyRepository.save(property);
//        return mapToResponse(updated);
//    }
//
//    // ✅ Helper for creation
//    private Property buildPropertyFromRequest(PropertyRequest req, User owner) {
//        Set<Amenity> amenities = new HashSet<>();
//        if (req.getAmenityIds() != null && !req.getAmenityIds().isEmpty()) {
//            amenities.addAll(amenityRepository.findAllById(req.getAmenityIds()));
//        }
//
//        Property property = Property.builder()
//                .title(req.getTitle())
//                .description(req.getDescription())
//                .address(req.getAddress())
//                .houseNumber(req.getHouseNumber())
//                .city(req.getCity())
//                .district(req.getDistrict())
//                .price(req.getPrice())
//                .bedrooms(req.getBedrooms())
//                .latitude(req.getLatitude())
//                .longitude(req.getLongitude())
//                .owner(owner)
//                .amenities(amenities)
//                .build();
//
//        if (req.getImages() != null && !req.getImages().isEmpty()) {
//            List<PropertyImage> images = req.getImages().stream()
//                    .filter(Objects::nonNull)
//                    .map(imgStr -> {
//                        PropertyImage image = new PropertyImage();
//                        image.setProperty(property);
//                        image.setImageData(Base64.getDecoder().decode(imgStr));
//                        return image;
//                    }).collect(Collectors.toList());
//            property.setImages(images);
//        } else {
//            property.setImages(new ArrayList<>());
//        }
//
//        return property;
//    }
//
//    // ✅ Other existing methods (deleteProperty, getAll, getById, etc.) remain same
//    
//    ///////////////////////////////////////////////////////////////////////////////////////
//    // ============================================================
//    // ✅ GET ALL PROPERTIES
//    // ============================================================
//    public List<PropertyResponse> getAllProperties() {
//        List<Property> properties = propertyRepository.findAll();
//
//        // ⚙️ Debug tip — prints image sizes (null-safe)
//        for (Property p : properties) {
//            for (PropertyImage image : p.getImages()) {
//                System.out.println("✅ Property ID " + p.getId() +
//                    " → image bytes length: " +
//                    (image.getImageData() != null ? image.getImageData().length : "null"));
//            }
//        }
//
//        return properties.stream()
//                .map(this::mapToResponse)
//                .toList();
//    }
//
//    // ============================================================
//    // ✅ GET PROPERTY BY ID
//    // ============================================================
//    public PropertyResponse getPropertyById(Long id) {
//        Property property = propertyRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Property not found"));
//        return mapToResponse(property);
//    }
//
//    // ============================================================
//    // ✅ SEARCH PROPERTY BY CITY
//    // ============================================================
//    public List<PropertyResponse> searchByCity(String city) {
//        return propertyRepository.findByCityIgnoreCase(city).stream()
//                .map(this::mapToResponse)
//                .toList();
//    }
//
//    // ============================================================
//    // ✅ GET PROPERTIES BY OWNER ID
//    // ============================================================
//    public List<PropertyResponse> getPropertiesByOwnerId(Long ownerId) {
//        return propertyRepository.findByOwnerId(ownerId).stream()
//                .map(this::mapToResponse)
//                .toList();
//    }
//
//    // ============================================================
//    // ✅ DELETE PROPERTY
//    // ============================================================
//    /**
//     * ✅ Delete property:
//     *  - SELLER can delete only their own property
//     *  - ADMIN can delete any property
//     */
//    public void deleteProperty(Long id, String username) {
//        Property property = propertyRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Property not found"));
//
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // 🔒 Only seller's own property or admin
//        if (user.getRole() == User.Role.SELLER &&
//                !property.getOwner().getUsername().equals(username)) {
//            throw new RuntimeException("You can only delete your own property");
//        }
//
//        // ✅ Admin can delete any property
//        propertyRepository.delete(property);
//    }
//    
//    // ============================================================
//    // ✅ ADMIN: ALL PROPERTIES WITH OWNERS
//    // ============================================================
//    public List<PropertyResponse> getAllPropertiesWithOwners() {
//        return propertyRepository.findAll().stream()
//                .map(this::mapToResponse)
//                .toList();
//    }
//
//    // ============================================================
//    // ✅ MONTHLY EARNINGS
//    // ============================================================
//    public Map<String, Double> calculateMonthlyEarnings(String username) {
//        Map<String, Double> earnings = new LinkedHashMap<>();
//        List<Object[]> result = propertyRepository.findMonthlyEarningsByOwner(username);
//
//        for (Object[] row : result) {
//            Integer monthNumber = (Integer) row[0];
//            Double total = (Double) row[1];
//            String monthName = Month.of(monthNumber).name();
//            earnings.put(monthName, total);
//        }
//        return earnings;
//    }
//  
//    // ✅ Mapper
//    private PropertyResponse mapToResponse(Property p) {
//        PropertyResponse dto = new PropertyResponse();
//        dto.setId(p.getId());
//        dto.setTitle(p.getTitle());
//        dto.setDescription(p.getDescription());
//        dto.setAddress(p.getAddress());
//        dto.setHouseNumber(p.getHouseNumber());
//        dto.setCity(p.getCity());
//        dto.setDistrict(p.getDistrict());
//        dto.setPrice(p.getPrice());
//        dto.setBedrooms(p.getBedrooms());
//        dto.setLatitude(p.getLatitude());
//        dto.setLongitude(p.getLongitude());
//        dto.setAmenities(p.getAmenities());
//        dto.setImages(p.getImages().stream().map(PropertyImageResponse::new).toList());
//
//        if (p.getOwner() != null) {
//            OwnerResponse owner = new OwnerResponse();
//            owner.setId(p.getOwner().getId());
//            owner.setUsername(p.getOwner().getUsername());
//            owner.setEmail(p.getOwner().getEmail());
//            owner.setFullName(p.getOwner().getFullName());
//            owner.setRole(p.getOwner().getRole().name());
//            dto.setOwner(owner);
//        }
//        return dto;
//    }
//    
// // ============================================================
// // ✅ DELETE SINGLE IMAGE (FIXED)
// // ============================================================
// public void deleteImage(Long imageId, String username) {
//     
//     // 1. Fetch the PropertyImage entity (must be managed)
//     PropertyImage image = propertyImageRepository.findById(imageId)
//             .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));
//
//     // 2. Fetch the User entity for role/owner check
//     User user = userRepository.findByUsername(username)
//             .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
//
//     Property property = image.getProperty(); // Get the parent entity
//
//     // 3. Authorization Check (Ensures SELLER/ADMIN logic is correct)
//     String ownerUsername = property.getOwner().getUsername();
//     boolean isOwner = username.equals(ownerUsername);
//     boolean isAdmin = user.getRole().name().equals("ADMIN"); 
//     // Assuming User.Role is an Enum, use .name() for String comparison.
//
//     if (!isOwner && !isAdmin) {
//         throw new RuntimeException("Access denied: You can only delete your own property images (or need Admin privileges).");
//     }
//
//     // 💥 CRITICAL JPA FIX 💥
//     // Remove the image from the parent entity's collection.
//     // This triggers the database DELETE via orphanRemoval=true when the transaction commits.
//     if (property.getImages() != null) {
//         property.getImages().remove(image);
//     }
//     
//     // We rely on the implicit transaction to save the change to the managed 'property' entity.
//     // To solve the immediate stale data issue, we force a flush:
//     propertyImageRepository.flush();
//     
//     // NOTE: We no longer call propertyImageRepository.delete(image) directly.
//     // The collection removal handles the actual database deletion.
// }
//
//}
