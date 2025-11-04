package com.nayabas.dto;

import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class PropertyRequest {
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
    private Set<Long> amenityIds;   
    private List<String> images;   
}
