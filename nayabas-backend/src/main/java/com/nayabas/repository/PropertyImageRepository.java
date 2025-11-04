package com.nayabas.repository;

import com.nayabas.entity.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {
    List<PropertyImage> findByPropertyId(Long propertyId);
}