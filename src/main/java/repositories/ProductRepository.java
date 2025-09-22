package com.restaurant.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.dtos.CategoryDTO;
import com.restaurant.dtos.ProductDTO;
import com.restaurant.entities.Category;
import com.restaurant.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findAllByCategoryId(Long categoryId);

	List<Product> findAllByCategoryIdAndNameContaining(Long categoryId, String title);

}
