package com.example.ecommerce_system.util.mapper;

import com.example.ecommerce_system.model.Role;
import com.example.ecommerce_system.model.RoleType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    default RoleType toRoleType(Role role) {
        return role != null ? role.getRoleName() : null;
    }
}
