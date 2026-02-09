package com.example.ecommerce_system.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Table(name = "cart")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Cart {
    @Id
    @Column(name = "cart_id")
    private UUID cartId;

    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> cartItems;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
