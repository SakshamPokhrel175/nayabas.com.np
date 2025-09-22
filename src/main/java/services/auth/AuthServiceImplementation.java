package com.restaurant.services.auth;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.restaurant.dtos.SignupRequestDTO;
import com.restaurant.dtos.UserDTO;
import com.restaurant.entities.User;
import com.restaurant.enums.UserRole_enum;
import com.restaurant.repositories.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
//    private final UserMapper userMapper; // Injected mapper

	@PostConstruct
	public void createAdminAccount() {
		Optional<User> adminAccount = userRepository.findFirstByUserRole(UserRole_enum.ADMIN);
		if (adminAccount.isEmpty()) {
			User user = new User();
			user.setFirstName("admin");
			user.setLastName("admin");
			user.setUsername("admin");
			user.setEmail("admin@gmail.com");
			user.setPassword(passwordEncoder.encode("admin"));
			user.setUserRole(UserRole_enum.ADMIN);
			userRepository.save(user);
		}
	}

	@Override
	public UserDTO createUser(SignupRequestDTO signupRequest) {
		User user = new User();
		user.setFirstName(signupRequest.getFirstName());
		user.setLastName(signupRequest.getLastName());
		user.setEmail(signupRequest.getEmail());
		user.setUsername(signupRequest.getUsername());
		user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
		user.setUserRole(UserRole_enum.CUSTOMER);
		User createdUser = userRepository.save(user);

		UserDTO createduserDto = new UserDTO();
		createduserDto.setId(createdUser.getId());
		createduserDto.setFirstName(createdUser.getFirstName());
		createduserDto.setLastName(createdUser.getLastName());
		createduserDto.setUsername(createdUser.getUsername());
		createduserDto.setEmail(createdUser.getEmail());
		createduserDto.setUserRole(createdUser.getUserRole());
		return createduserDto;
	}

}
