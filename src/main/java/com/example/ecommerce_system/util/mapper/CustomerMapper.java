package com.example.ecommerce_system.util.mapper;

import com.example.ecommerce_system.dto.customer.CustomerResponseDto;
import com.example.ecommerce_system.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.createdAt", target = "createdAt")
    CustomerResponseDto toDTO(Customer customer);

    List<CustomerResponseDto> toDTOList(List<Customer> customers);
}
