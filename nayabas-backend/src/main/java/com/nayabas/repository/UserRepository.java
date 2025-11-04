package com.nayabas.repository;

import com.nayabas.entity.User;
import com.nayabas.entity.User.Role;
import com.nayabas.entity.User.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    List<User> findByRole(User.Role role);
    List<User> findByRoleAndStatus(User.Role role, User.Status status);

    long countByRole(User.Role role);
    long countByRoleAndStatus(User.Role role, User.Status status);
}
