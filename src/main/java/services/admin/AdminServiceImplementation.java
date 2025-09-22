package com.restaurant.services.admin;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.restaurant.dtos.CategoryDTO;
import com.restaurant.dtos.ProductDTO;
import com.restaurant.entities.Category;
import com.restaurant.entities.Product;
import com.restaurant.repositories.CategoryRepository;
import com.restaurant.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImplementation implements AdminService {

	private final CategoryRepository categoryRepository;

	private final ProductRepository productRepository;

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

	@Override
	public List<CategoryDTO> getAllCategories() {

		return categoryRepository.findAll().stream().map(Category::getCategoryDTO).collect(Collectors.toList());
	}

	@Override
	public List<CategoryDTO> getAllCategoriesByTitle(String title) {
		// TODO Auto-generated method stub
		return categoryRepository.findAllByNameContaining(title).stream().map(Category::getCategoryDTO)
				.collect(Collectors.toList());
	}

//Product Operations
	@Override
	public ProductDTO postProduct(Long categoryId, ProductDTO productDTO) throws IOException {
		Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
		if (optionalCategory.isPresent()) {
			Product product = new Product();
			BeanUtils.copyProperties(productDTO, product);
			product.setImg(productDTO.getImg().getBytes());
			product.setCategory(optionalCategory.get());
			Product createdProduct = productRepository.save(product);
			ProductDTO createdproductDTO = new ProductDTO();
			createdproductDTO.setId(createdProduct.getId());
			return createdproductDTO;

		}
		return null;
	}

	@Override
	public List<ProductDTO> getAllProductsByCategory(Long categoryId) {
		return productRepository.findAllByCategoryId(categoryId).stream().map(Product::getProductDTO)
				.collect(Collectors.toList());
	}

	@Override
	public List<ProductDTO> getProductsByCategoryAndTitle(Long categoryId, String title) {

		return productRepository.findAllByCategoryIdAndNameContaining(categoryId, title).stream()
				.map(Product::getProductDTO).collect(Collectors.toList());
	}

	@Override
	public void deleteProduct(Long productId) {
		// TODO Auto-generated method stub
		Optional<Product> optionalProduct = productRepository.findById(productId);
		if (optionalProduct.isPresent()) {
			productRepository.deleteById(productId);
		}
		throw new IllegalArgumentException("Product with id: " + productId + " not found");

	}
	
	

}
