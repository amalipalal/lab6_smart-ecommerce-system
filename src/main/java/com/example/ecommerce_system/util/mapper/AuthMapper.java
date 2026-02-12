package com.example.ecommerce_system.util.mapper;

import com.example.ecommerce_system.dto.auth.AuthResponseDto;
import com.example.ecommerce_system.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface AuthMapper {

    @Mapping(source = "user.role", target = "roleName")
    AuthResponseDto toDTO(User user, String token);
}
