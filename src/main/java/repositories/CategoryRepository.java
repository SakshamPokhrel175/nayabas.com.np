package com.restaurant.repositories;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.dtos.CategoryDTO;
import com.restaurant.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findAllByNameContaining(String title);

}
