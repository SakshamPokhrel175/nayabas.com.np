package com.nayabas.repository;

import com.nayabas.entity.Property;
import com.nayabas.entity.PropertyImage;
import com.nayabas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    /** ✅ Find all properties in a city (case-insensitive) */
    List<Property> findByCityIgnoreCase(String city);

    /** ✅ Find all properties by owner ID */
    List<Property> findByOwnerId(Long ownerId);

    /** ✅ Find all properties by owner entity */
    List<Property> findByOwner(User owner);

    /** ✅ Optional: Search by city or district */
    List<Property> findByCityIgnoreCaseOrDistrictIgnoreCase(String city, String district);

    /** ✅ Optional: Find all properties sorted by created date (for admin dashboard) */
    List<Property> findAllByOrderByCreatedAtDesc();

    /** ✅ Custom query: Calculate total earnings grouped by month (for seller earnings chart) */
    @Query("SELECT MONTH(p.createdAt), SUM(p.price) FROM Property p WHERE p.owner.username = :username GROUP BY MONTH(p.createdAt)")
    List<Object[]> findMonthlyEarningsByOwner(String username);
    
    @Query("SELECT i FROM PropertyImage i WHERE i.id = :imageId")
    Optional<PropertyImage> findImageById(@Param("imageId") Long imageId);

}