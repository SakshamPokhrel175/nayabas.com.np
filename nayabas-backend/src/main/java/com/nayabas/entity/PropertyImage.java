package com.nayabas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Uploaded image bytes only
    @Lob
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    @JsonIgnore
    private Property property;
}

//package com.nayabas.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class PropertyImage {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Lob // Large Object
//    @Column(columnDefinition = "LONGTEXT") // or "TEXT" if URLs are shorter
//    private String url;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "property_id")
//    private Property property;
//}
