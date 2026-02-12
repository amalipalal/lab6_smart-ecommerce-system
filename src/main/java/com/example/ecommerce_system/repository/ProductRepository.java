package com.example.ecommerce_system.repository;

import com.example.ecommerce_system.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    @Query(value = """
        SELECT DISTINCT p.* FROM product p
        LEFT JOIN category c ON p.category_id = c.category_id
        LEFT JOIN review r ON p.product_id = r.product_id
        WHERE r.review_id IN (
            SELECT r2.review_id FROM review r2
            WHERE r2.product_id = p.product_id
            ORDER BY r2.created_at DESC
            LIMIT :reviewLimit
        ) OR NOT EXISTS (
            SELECT 1 FROM review r3 WHERE r3.product_id = p.product_id
        )
        """,
        countQuery = """
            SELECT COUNT(DISTINCT p.product_id) FROM product p
            """,
        nativeQuery = true)
    Page<Product> findAllWithLimitedReviews(@Param("reviewLimit") int reviewLimit, Pageable pageable);
}
