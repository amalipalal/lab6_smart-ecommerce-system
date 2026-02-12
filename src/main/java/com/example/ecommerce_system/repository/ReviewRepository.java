package com.example.ecommerce_system.repository;

import com.example.ecommerce_system.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findAllByProduct_ProductId(UUID productProductId, Pageable pageable);

    Page<Review> findAllByCustomer_CustomerId(UUID customerCustomerId, Pageable pageable);
}
