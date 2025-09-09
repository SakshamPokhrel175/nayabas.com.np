package com.rental.USER;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false, updatable = false)
	private Long id;

	@NotBlank
	@Column(name = "firstName")
	private String firstName;
	@NotBlank
	@Column(name = "lastName")
	private String lastName;
	@Email
	@NotBlank
	@Column(name = "email")
	private String email;
	@NotBlank
	@Column(name = "username", unique = true, nullable = false, updatable = false)
	private String username;
	@NotBlank
	@Column(name = "password")
	private String password;

	@Column(name = "userRole")
	private UserRole_enum userRole;

}
