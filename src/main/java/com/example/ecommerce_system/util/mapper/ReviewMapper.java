package com.example.ecommerce_system.util.mapper;

import com.example.ecommerce_system.dto.review.ReviewResponseDto;
import com.example.ecommerce_system.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for Review entity.
 * Maps Review entity to ReviewResponseDto with customer details.
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper {

    /**
     * Convert Review entity to ReviewResponseDto.
     * Note: customer field must be set manually after mapping since Review only has customerId.
     *
     * @param review the Review entity
     * @return ReviewResponseDto with review details (customer must be set separately)
     */
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(target = "customer", ignore = true)  // Must be set manually in service
    ReviewResponseDto toDTO(Review review);

    /**
     * Convert list of Review entities to list of ReviewResponseDto.
     * Note: customer field must be set manually for each review.
     *
     * @param reviews list of Review entities
     * @return list of ReviewResponseDto (customers must be set separately)
     */
    List<ReviewResponseDto> toDTOList(List<Review> reviews);
}
