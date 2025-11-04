package com.nayabas.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Long propertyId;
    private Integer rating;
    private String comment;
}