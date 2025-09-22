package com.restaurant.entities;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.restaurant.enums.UserRole_enum;

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
public class User implements UserDetails {

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRole.name())); // e.g., "ADMIN"
    }


    
    @Override
    public String getUsername() {
    	return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // change if you want to track expiry
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // change if you want to track locked accounts
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // change if you want to track credential expiry
    }

    @Override
    public boolean isEnabled() {
        return true; // change if you want to track active/inactive users
    }
}
