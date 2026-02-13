package com.example.ecommerce_system.dto.product;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductFilter {
    private String name;
    private String description;
    private UUID categoryId;
    private Double minPrice;
    private Double maxPrice;
    private Integer minStock;
    private Integer maxStock;

    public boolean hasName() {
        return this.name != null;
    }

    public boolean hasCategoryId() {
        return this.categoryId != null;
    }

    public boolean hasDescription() {
        return this.description != null;
    }

    public boolean hasMinPrice() {
        return this.minPrice != null;
    }

    public boolean hasMaxPrice() {
        return this.maxPrice != null;
    }

    public boolean hasMinStock() {
        return this.minStock != null;
    }

    public boolean hasMaxStock() {
        return this.maxStock != null;
    }

    public boolean isEmpty() {
        return !hasName() && !hasCategoryId() && !hasDescription()
                && !hasMinPrice() && !hasMaxPrice()
                && !hasMinStock() && !hasMaxStock();
    }
}
