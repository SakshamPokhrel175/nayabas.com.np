package com.rental.USER;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO {
	
	private String firstName;
	private String lastName;
	private String email;
	private String username;
	private String password;

}
