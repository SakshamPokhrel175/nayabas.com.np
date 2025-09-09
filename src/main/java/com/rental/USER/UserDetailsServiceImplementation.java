package com.rental.USER;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailsServiceImplementation(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Optional<User> optionalUser = userRepository.findFirstByEmail(email);

		if (optionalUser.isEmpty())
			throw new UsernameNotFoundException("User not found", null);
		return new org.springframework.security.core.userdetails.User(optionalUser.get().getEmail(),
				optionalUser.get().getPassword(), new ArrayList<>());
	}

}
