package com.restaurant.dtos;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class CategoryDTO {
	
	private Long id; 
	private String name; 
	private String description; 
	
	// for receiving an image file in a request (e.g. form-data from frontend)
	private MultipartFile img;

	// for sending the stored image back to the frontend (as byte[])
	private byte[] returnedImg;
}
