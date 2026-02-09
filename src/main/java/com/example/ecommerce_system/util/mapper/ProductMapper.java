package com.example.ecommerce_system.util.mapper;

import com.example.ecommerce_system.dto.product.ProductResponseDto;
import com.example.ecommerce_system.dto.product.ProductWithReviewsDto;
import com.example.ecommerce_system.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for Product entity.
 * Maps Product entity to various Product DTOs.
 */
@Mapper(componentModel = "spring", uses = {CategoryMapper.class, ReviewMapper.class})
public interface ProductMapper {

    /**
     * Convert Product entity to ProductResponseDto.
     * Maps category.categoryId and stockQuantity to stock.
     *
     * @param product the Product entity
     * @return ProductResponseDto with product details
     */
    @Mapping(source = "category.categoryId", target = "categoryId")
    @Mapping(source = "stockQuantity", target = "stock")
    ProductResponseDto toDTO(Product product);

    /**
     * Convert list of Product entities to list of ProductResponseDto.
     *
     * @param products list of Product entities
     * @return list of ProductResponseDto
     */
    List<ProductResponseDto> toDTOList(List<Product> products);

    /**
     * Convert Product entity to ProductWithReviewsDto.
     * Maps full category object and stockQuantity to stock.
     * Note: reviews list must be set separately as Review entities need customer lookup.
     *
     * @param product the Product entity
     * @return ProductWithReviewsDto with product and category details
     */
    @Mapping(source = "category", target = "category")
    @Mapping(source = "stockQuantity", target = "stock")
    @Mapping(target = "reviews", ignore = true)  // Must be set manually in service
    ProductWithReviewsDto toProductWithReviewsDTO(Product product);

    /**
     * Convert list of Product entities to list of ProductWithReviewsDto.
     * Note: reviews must be set separately for each product.
     *
     * @param products list of Product entities
     * @return list of ProductWithReviewsDto
     */
    List<ProductWithReviewsDto> toProductWithReviewsDTOList(List<Product> products);
}
