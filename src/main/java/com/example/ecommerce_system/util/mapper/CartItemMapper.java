package com.example.ecommerce_system.util.mapper;

import com.example.ecommerce_system.dto.cart.CartItemResponseDto;
import com.example.ecommerce_system.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface CartItemMapper {

    @Mapping(target = "cartId", source = "cart.cartId")
    CartItemResponseDto toDTO(CartItem cartItem);

    List<CartItemResponseDto> toDTOList(List<CartItem> cartItems);
}
