package com.example.ecommerce_system.util;

import com.example.ecommerce_system.model.CartItem;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class CartItemSpecification {

    public static Specification<CartItem> belongsToCart(UUID cartId) {
        return (root, query, cb) ->
                cb.equal(root.get("cart").get("cartId"), cartId);
    }

    public static Specification<CartItem> productNameContains(String searchTerm) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("product").get("name")), "%" + searchTerm.toLowerCase() + "%");
    }

    public static Specification<CartItem> productDescriptionContains(String searchTerm) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("product").get("description")), "%" + searchTerm.toLowerCase() + "%");
    }

    public static Specification<CartItem> searchByProductNameOrDescription(UUID cartId, String searchTerm) {
        Specification<CartItem> spec = (root, query, criteriaBuilder) -> null;
        spec = spec
                .and(belongsToCart(cartId))
                .and(productNameContains(searchTerm))
                .or(productDescriptionContains(searchTerm));
        return spec;
    }
}
