package com.rental.USER;

import java.io.IOException;
import java.lang.StackWalker.Option;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rental.util.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;

	@PostMapping("/signup")
	public ResponseEntity<?> signupUser(@RequestBody SignupRequestDTO signupRequest) {
		UserDTO createduserDto = userService.createUser(signupRequest);
		if (createduserDto == null) {
			return new ResponseEntity<>("User not created. Come again later", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(createduserDto, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public AuthenticationResponseDTO createAuthenticationToken(
			@RequestBody AuthenticationRequestDTO authenticationRequestDTO, HttpServletResponse response)
			throws IOException {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequestDTO.getEmail(), authenticationRequestDTO.getPassword()));
		} catch (BadCredentialsException e) {
			throw new BadCredentialsException("Incorrect username or password");
		} catch (DisabledException disabledException) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not active");
			return null;
		}

		// Load user details of the authenticated user
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequestDTO.getEmail());

		// Generate the JWT token
		final String jwt = jwtUtil.generateToken(userDetails.getUsername());
		
		Optional<User> optionalUser =userRepository.findFirstByEmail(userDetails.getUsername());
		AuthenticationResponseDTO  authenticationResponse=new AuthenticationResponseDTO();
		if(optionalUser.isPresent()) {
			authenticationResponse.setJwt(jwt);
			authenticationResponse.setUserRole(optionalUser.get().getUserRole());
			authenticationResponse.setUserId(optionalUser.get().getId());
		}
		
		// Return the token inside an AuthenticationResponseDTO
		return authenticationResponse;
	}
}
