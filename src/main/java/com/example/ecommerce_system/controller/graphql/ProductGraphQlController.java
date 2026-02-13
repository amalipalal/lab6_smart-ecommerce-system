package com.example.ecommerce_system.controller.graphql;

import com.example.ecommerce_system.dto.product.ProductFilter;
import com.example.ecommerce_system.dto.product.ProductWithReviewsDto;
import com.example.ecommerce_system.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class ProductGraphQlController {

    private final ProductService productService;

    /**
     * Retrieves all products with their reviews.
     * Supports optional filtering by name, description, category, price range, and stock range.
     */
    @QueryMapping
    public List<ProductWithReviewsDto> getAllProductsWithReviews(
            @Argument int limit,
            @Argument int offset,
            @Argument(name = "reviewLimit") int reviewLimit,
            @Argument String name,
            @Argument String description,
            @Argument UUID categoryId,
            @Argument Double minPrice,
            @Argument Double maxPrice,
            @Argument Integer minStock,
            @Argument Integer maxStock
    ) {
        final int MAX_REVIEW_LIMIT = 50;
        int effectiveReviewLimit = Math.min(reviewLimit, MAX_REVIEW_LIMIT);

        ProductFilter filter = ProductFilter.builder()
                .name(name)
                .description(description)
                .categoryId(categoryId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minStock(minStock)
                .maxStock(maxStock)
                .build();

        return filter.isEmpty()
                ? productService.getAllProductsWithReviews(limit, offset, effectiveReviewLimit)
                : productService.searchProductsWithReviews(filter, limit, offset);
    }
}
