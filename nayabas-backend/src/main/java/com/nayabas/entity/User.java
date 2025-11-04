package com.nayabas.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "properties", "meetings"})
public class User {

	public enum Role {
		ADMIN, CUSTOMER, SELLER
	}

	public enum Status {
		PENDING, APPROVED, REJECTED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, unique = true)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status; // ✅ new field

	private String fullName;
	private String phoneNumber;
	private String addressLine1;

	private Boolean isVerified;

	@Lob
	@Column(name = "profile_image", columnDefinition = "LONGBLOB")
	private byte[] profileImage;

	@Lob
	@Column(name = "id_proof", columnDefinition = "LONGBLOB")
	private byte[] idProof;

	@Lob
	@Column(name = "house_ownership_proof", columnDefinition = "LONGBLOB")
	private byte[] houseOwnershipProof;

	private String verificationNotes;
	
	
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"owner", "hibernateLazyInitializer", "handler"})
    @JsonManagedReference
    private List<Property> properties = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"customer", "hibernateLazyInitializer", "handler"})
    private List<Meeting> meetings = new ArrayList<>();

}