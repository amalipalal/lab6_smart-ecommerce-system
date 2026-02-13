package com.example.ecommerce_system.util;

import com.example.ecommerce_system.dto.product.ProductFilter;
import com.example.ecommerce_system.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

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

        return spec
                .and(buildIfPresent(filter::hasName, () -> nameContains(filter.getName())))
                .and(buildIfPresent(filter::hasDescription, () -> descriptionContains(filter.getDescription())))
                .and(buildIfPresent(filter::hasCategoryId, () -> hasCategory(filter.getCategoryId())))
                .and(buildIfPresent(filter::hasMinPrice, () -> priceGreaterThanOrEqual(filter.getMinPrice())))
                .and(buildIfPresent(filter::hasMaxPrice, () -> priceLessThanOrEqual(filter.getMaxPrice())))
                .and(buildIfPresent(filter::hasMinStock, () -> stockGreaterThanOrEqual(filter.getMinStock())))
                .and(buildIfPresent(filter::hasMaxStock, () -> stockLessThanOrEqual(filter.getMaxStock())));
    }

    private static Specification<Product> buildIfPresent(
            BooleanSupplier condition,
            Supplier<Specification<Product>> specSupplier) {
        return condition.getAsBoolean() ? specSupplier.get() : null;
    }
}
