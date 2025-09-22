package com.restaurant.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.restaurant.entities.User;
import com.restaurant.enums.UserRole_enum;

@Repository
@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findFirstByEmail(String email);

	Optional<User> findFirstByUserRole(UserRole_enum userRole);

}
