package com.example.ecommerce_system.controller.graphql;

import com.example.ecommerce_system.dto.cart.AddCartItem;
import com.example.ecommerce_system.dto.cart.CartItemRequestDto;
import com.example.ecommerce_system.dto.cart.CartItemResponseDto;
import com.example.ecommerce_system.dto.cart.UpdateCartItem;
import com.example.ecommerce_system.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class CartGraphQLController {
    private final CartService cartService;

    @QueryMapping
    public List<CartItemResponseDto> getCustomerCartItems(@Argument String userId) {
        UUID userUuid = UUID.fromString(userId);
        return cartService.getCartItemsByCustomer(userUuid);
    }

    @MutationMapping
    public CartItemResponseDto addCartItem(
            @Argument String userId,
            @Argument @Validated(AddCartItem.class) CartItemRequestDto request) {
        UUID userUuid = UUID.fromString(userId);
        return cartService.addToCart(userUuid, request);
    }

    @MutationMapping
    public CartItemResponseDto updateCartItem(
            @Argument String userId,
            @Argument String cartItemId,
            @Argument @Validated(UpdateCartItem.class) CartItemRequestDto request) {
        UUID userUuid = UUID.fromString(userId);
        UUID cartItemUuid = UUID.fromString(cartItemId);
        return cartService.updateCartItem(userUuid, cartItemUuid, request);
    }

    @MutationMapping
    public Boolean removeFromCart(
            @Argument String userId,
            @Argument String cartItemId) {
        UUID userUuid = UUID.fromString(userId);
        UUID cartItemUuid = UUID.fromString(cartItemId);
        cartService.removeFromCart(userUuid, cartItemUuid);
        return true;
    }
}
