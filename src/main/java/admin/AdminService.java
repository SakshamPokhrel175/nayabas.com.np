package com.restaurant.services.admin;

import java.io.IOException;

import com.restaurant.dtos.CategoryDTO;

public interface AdminService {

	CategoryDTO postCategory(CategoryDTO categoryDTO) throws IOException;

}
