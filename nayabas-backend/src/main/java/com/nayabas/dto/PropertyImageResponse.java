package com.nayabas.dto;


import java.util.Base64;
import com.nayabas.entity.PropertyImage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyImageResponse {
    private Long id;
    private String imageData; // plain Base64 only

    public PropertyImageResponse(PropertyImage image) {
        this.id = image.getId();
        this.imageData = image.getImageData() != null
                ? Base64.getEncoder().encodeToString(image.getImageData())
                : null;
    }
}