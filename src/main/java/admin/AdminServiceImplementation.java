package com.restaurant.services.admin;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.restaurant.dtos.CategoryDTO;
import com.restaurant.entities.Category;
import com.restaurant.repositories.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImplementation implements AdminService{
	
	private final CategoryRepository categoryRepository;

	@Override
	public CategoryDTO postCategory(CategoryDTO categoryDTO) throws IOException {
	 
	        Category category = new Category();
	        category.setName(categoryDTO.getName());
	        category.setDescription(categoryDTO.getDescription());
	        category.setImg(categoryDTO.getImg().getBytes());
	       
	        Category createdCategory = categoryRepository.save(category);
	        
	        CategoryDTO createdCategoryDTO = new CategoryDTO();
	        createdCategoryDTO.setId(createdCategory.getId());
//	        createdCategoryDTO.setName(createdCategory.getName());
//	        createdCategoryDTO.setDescription(createdCategory.getDescription());
//	        createdCategoryDTO.setReturnedImg(createdCategory.getImg());

	        return createdCategoryDTO;
	}


}
