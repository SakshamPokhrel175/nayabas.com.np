package com.restaurant.entities;

import com.restaurant.dtos.CategoryDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false, updatable = false)
	private Long id;

	@NotBlank
	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@NotBlank
	@Column(name = "description")
	private String description;

	@Lob
	@Column(name = "img", columnDefinition = "longblob")
	private byte[] img;

	public CategoryDTO getCategoryDTO() {
		CategoryDTO categoryDTO = new CategoryDTO();
		categoryDTO.setId(id);
		categoryDTO.setName(name);
		categoryDTO.setDescription(description);
		categoryDTO.setReturnedImg(img);
		return categoryDTO;

	}
}
