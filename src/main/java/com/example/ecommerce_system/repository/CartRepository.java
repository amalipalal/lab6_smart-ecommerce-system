package com.example.ecommerce_system.repository;

import com.example.ecommerce_system.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findCartByCustomer_CustomerId(UUID customerCustomerId);
}
