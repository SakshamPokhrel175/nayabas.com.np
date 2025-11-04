package com.nayabas.dto;
import com.nayabas.entity.Amenity;
import com.nayabas.entity.PropertyImage;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class PropertyResponse {
    private Long id;
    private String title;
    private String description;
    private String address;
    private String houseNumber;
    private String city;
    private String district;
    private Double price;
    private Integer bedrooms;
    private Double latitude;
    private Double longitude;
    private Set<Amenity> amenities;
    private List<PropertyImageResponse> images = new ArrayList<>();
    private OwnerResponse owner;
}