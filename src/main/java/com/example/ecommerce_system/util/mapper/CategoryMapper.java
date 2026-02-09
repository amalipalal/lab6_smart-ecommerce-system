package com.example.ecommerce_system.util.mapper;

import com.example.ecommerce_system.dto.category.CategoryResponseDto;
import com.example.ecommerce_system.model.Category;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper for Category entity.
 * Maps Category entity to CategoryResponseDto.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Convert Category entity to CategoryResponseDto.
     *
     * @param category the Category entity
     * @return CategoryResponseDto with all category details
     */
    CategoryResponseDto toDTO(Category category);

    /**
     * Convert list of Category entities to list of CategoryResponseDto.
     *
     * @param categories list of Category entities
     * @return list of CategoryResponseDto
     */
    List<CategoryResponseDto> toDTOList(List<Category> categories);
}
