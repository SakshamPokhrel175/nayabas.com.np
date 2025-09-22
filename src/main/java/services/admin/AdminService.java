package com.restaurant.services.admin;

import java.io.IOException;
import java.util.List;

import com.restaurant.dtos.CategoryDTO;
import com.restaurant.dtos.ProductDTO;

public interface AdminService {

	CategoryDTO postCategory(CategoryDTO categoryDTO) throws IOException;

	List<CategoryDTO> getAllCategories();

	List<CategoryDTO> getAllCategoriesByTitle(String title);

	ProductDTO postProduct(Long categoryId, ProductDTO productDTO) throws IOException;

	List<ProductDTO> getAllProductsByCategory(Long categoryId);

	List<ProductDTO> getProductsByCategoryAndTitle(Long categoryId , String title);

	void deleteProduct(Long productId);

}
