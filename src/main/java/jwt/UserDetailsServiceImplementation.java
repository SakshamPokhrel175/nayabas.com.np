/*
 * package com.restaurant.services.jwt;
 * 
 * import java.util.ArrayList; import java.util.Optional;
 * 
 * import org.springframework.security.core.userdetails.UserDetails; import
 * org.springframework.security.core.userdetails.UserDetailsService; import
 * org.springframework.security.core.userdetails.UsernameNotFoundException;
 * import org.springframework.stereotype.Service;
 * 
 * import com.restaurant.entities.User; import
 * com.restaurant.repositories.UserRepository;
 * 
 * @Service public class UserDetailsServiceImplementation implements
 * UserDetailsService {
 * 
 * private final UserRepository userRepository;
 * 
 * public UserDetailsServiceImplementation(UserRepository userRepository) {
 * this.userRepository = userRepository; }
 * 
 * @Override public UserDetails loadUserByUsername(String email) throws
 * UsernameNotFoundException {
 * 
 * Optional<User> optionalUser = userRepository.findFirstByEmail(email);
 * 
 * if (optionalUser.isEmpty()) throw new
 * UsernameNotFoundException("User not found", null); return new
 * org.springframework.security.core.userdetails.User(optionalUser.get().
 * getEmail(), optionalUser.get().getPassword(), new ArrayList<>()); }
 * 
 * }
 */