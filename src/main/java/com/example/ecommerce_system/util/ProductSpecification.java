package com.example.ecommerce_system.util;

import com.example.ecommerce_system.dto.product.ProductFilter;
import com.example.ecommerce_system.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ProductSpecification {

    public static Specification<Product> nameContains(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasCategory(UUID categoryId) {
        return (root, query, cb) ->
                cb.equal(root.get("category").get("categoryId"), categoryId);
    }

    public static Specification<Product> descriptionContains(String description) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    public static Specification<Product> priceGreaterThanOrEqual(Double minPrice) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> priceLessThanOrEqual(Double maxPrice) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Product> stockGreaterThanOrEqual(Integer minStock) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("stockQuantity"), minStock);
    }

    public static Specification<Product> stockLessThanOrEqual(Integer maxStock) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("stockQuantity"), maxStock);
    }

    public static Specification<Product> buildSpecification(ProductFilter filter) {
        Specification<Product> spec = (root, query, criteriaBuilder) -> null;

        if (filter.hasName()) {
            spec = spec.and(nameContains(filter.getName()));
        }

        if (filter.hasCategoryId()) {
            spec = spec.and(hasCategory(filter.getCategoryId()));
        }

        if (filter.hasDescription()) {
            spec = spec.and(descriptionContains(filter.getDescription()));
        }

        if (filter.hasMinPrice()) {
            spec = spec.and(priceGreaterThanOrEqual(filter.getMinPrice()));
        }

        if (filter.hasMaxPrice()) {
            spec = spec.and(priceLessThanOrEqual(filter.getMaxPrice()));
        }

        if (filter.hasMinStock()) {
            spec = spec.and(stockGreaterThanOrEqual(filter.getMinStock()));
        }

        if (filter.hasMaxStock()) {
            spec = spec.and(stockLessThanOrEqual(filter.getMaxStock()));
        }

        return spec;
    }
}
