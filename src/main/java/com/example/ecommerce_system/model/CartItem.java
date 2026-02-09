package com.example.ecommerce_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Table(name = "cart_item")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CartItem {
    @Id
    @Column(name = "cart_item_id")
    private UUID cartItemId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    @Column(name = "added_at")
    private Instant addedAt;
}
