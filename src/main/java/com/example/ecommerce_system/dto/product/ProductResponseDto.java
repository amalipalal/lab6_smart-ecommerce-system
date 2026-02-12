package com.example.ecommerce_system.dto.product;

import com.example.ecommerce_system.dto.review.ReviewResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponseDto {
    private UUID productId;
    private UUID categoryId;
    private String name;
    private String description;
    private double price;
    private int stock;
    private Instant updatedAt;
    private List<ReviewResponseDto> reviews;
}
