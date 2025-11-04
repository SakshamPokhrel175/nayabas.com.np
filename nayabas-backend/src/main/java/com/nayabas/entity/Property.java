package com.nayabas.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//🛑 FIX: Tell Jackson to ignore Hibernate's internal proxy fields
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne(fetch = FetchType.EAGER) // optional, default is eager for ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonBackReference
    private User owner;

    @ManyToMany(fetch = FetchType.EAGER) // <--- EAGER ensures amenities are loaded for JSON
    @JoinTable(
        name = "property_amenities",
        joinColumns = @JoinColumn(name = "property_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) // <--- EAGER ensures images are loaded
    private List<PropertyImage> images = new ArrayList<>();

    
    // ✅ NEW: store when the property was created
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    
}
