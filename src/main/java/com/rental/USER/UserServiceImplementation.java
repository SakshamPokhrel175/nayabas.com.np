package com.rental.USER;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
//    private final UserMapper userMapper; // Injected mapper
	
	
	
	
	
	

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
