package com.restaurant.services.auth;

import com.restaurant.dtos.SignupRequestDTO;
import com.restaurant.dtos.UserDTO;

public interface AuthService {

	UserDTO createUser(SignupRequestDTO signupRequest);

}
