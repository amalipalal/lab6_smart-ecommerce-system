package com.example.ecommerce_system.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Data
@Builder
public class CustomerResponseDto {
    private UUID customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean active;
    private Instant createdAt;
}
