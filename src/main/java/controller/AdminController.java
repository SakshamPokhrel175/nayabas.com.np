package com.restaurant.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.restaurant.dtos.CategoryDTO;
import com.restaurant.dtos.ProductDTO;
import com.restaurant.services.admin.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	@PostMapping("/category")
	public ResponseEntity<CategoryDTO> postCategory(@ModelAttribute CategoryDTO categoryDTO) throws IOException {
		CategoryDTO createdCategoryDTO = adminService.postCategory(categoryDTO);
		if (createdCategoryDTO == null)
			return ResponseEntity.notFound().build();
		return ResponseEntity.ok(createdCategoryDTO);
	}
	
	
	@GetMapping("/categories")
	public ResponseEntity<List<CategoryDTO>> getAllCategories(){
		List<CategoryDTO> categoryDTOList = adminService.getAllCategories();
		if(categoryDTOList==null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(categoryDTOList);
		
	}
	
	@GetMapping("/categories/{title}")
	public ResponseEntity<List<CategoryDTO>> getAllCategoriesByTitle(@PathVariable String title){
		List<CategoryDTO> categoryDTOList = adminService.getAllCategoriesByTitle(title);
		if(categoryDTOList==null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(categoryDTOList);
		
	}
	
	
	
	
//	Product Operations
	
	@PostMapping("/{categoryId}/product")
	public ResponseEntity<?> postProduct(@PathVariable Long categoryId , @ModelAttribute ProductDTO productDTO) throws IOException {
		ProductDTO createdProductDTO = adminService.postProduct(categoryId,productDTO);
		if (createdProductDTO == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong !");
		return ResponseEntity.status(HttpStatus.CREATED).body(createdProductDTO);
	}
	
	
	
	@GetMapping("/{categoryId}/{products}")
	public ResponseEntity<List<ProductDTO>> getAllProductsByCategory(@PathVariable Long categoryId){
		List<ProductDTO> productDTOList = adminService.getAllProductsByCategory(categoryId);
		if(productDTOList==null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(productDTOList);
		
	}
	
	
	@GetMapping("/{categoryId}/product/{title}")
	public ResponseEntity<List<ProductDTO>> getProductsByCategoryAndTitle(@PathVariable Long categoryId, @PathVariable String title){
		List<ProductDTO> productDTOList = adminService.getProductsByCategoryAndTitle(categoryId,title);
		if(productDTOList==null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(productDTOList);
		
	}
	
	
	@DeleteMapping("/product/{productId}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long productId){
		adminService.deleteProduct(productId);
		return ResponseEntity.noContent().build();
		
	}
	
}
