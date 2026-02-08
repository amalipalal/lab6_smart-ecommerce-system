package com.example.ecommerce_system;

import com.example.ecommerce_system.dto.customer.CustomerRequestDto;
import com.example.ecommerce_system.dto.customer.CustomerResponseDto;
import com.example.ecommerce_system.exception.customer.CustomerNotFoundException;
import com.example.ecommerce_system.model.Customer;
import com.example.ecommerce_system.model.User;
import com.example.ecommerce_system.repository.CustomerRepository;
import com.example.ecommerce_system.service.CustomerService;
import com.example.ecommerce_system.store.CustomerStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerStore customerStore;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("Should get customer by id successfully")
    void shouldGetCustomerByIdSuccessfully() {
        UUID id = UUID.randomUUID();
        User user = User.builder()
                .email("john@example.com")
                .createdAt(Instant.now())
                .build();
        Customer customer = Customer.builder()
                .customerId(id)
                .user(user)
                .firstName("John")
                .lastName("Doe")
                .phone("+233123456789")
                .isActive(true)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        CustomerResponseDto response = customerService.getCustomer(id);

        Assertions.assertEquals(id, response.getCustomerId());
        Assertions.assertEquals("John", response.getFirstName());
        Assertions.assertEquals("john@example.com", response.getEmail());
        verify(customerRepository).findById(id);
    }

    @Test
    @DisplayName("Should throw error when customer not found by id")
    void shouldThrowWhenCustomerNotFoundById() {
        UUID id = UUID.randomUUID();

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomer(id)
        );

        verify(customerRepository).findById(id);
    }

    @Test
    @DisplayName("Should get all customers successfully")
    void shouldGetAllCustomersSuccessfully() {
        User user1 = User.builder().email("john@example.com").createdAt(Instant.now()).build();
        User user2 = User.builder().email("jane@example.com").createdAt(Instant.now()).build();
        List<Customer> customers = List.of(
                Customer.builder()
                        .customerId(UUID.randomUUID())
                        .user(user1)
                        .firstName("John")
                        .lastName("Doe")
                        .phone("+233123456789")
                        .isActive(true)
                        .build(),
                Customer.builder()
                        .customerId(UUID.randomUUID())
                        .user(user2)
                        .firstName("Jane")
                        .lastName("Smith")
                        .phone("+233987654321")
                        .isActive(true)
                        .build()
        );

        Page<Customer> page = new PageImpl<>(customers);
        when(customerRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        List<CustomerResponseDto> result = customerService.getAllCustomers(10, 0);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("John", result.get(0).getFirstName());
        Assertions.assertEquals("Jane", result.get(1).getFirstName());
        verify(customerRepository).findAll(PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("Should search customers successfully")
    void shouldSearchCustomersSuccessfully() {
        String query = "john";
        User user = User.builder().email("john@example.com").createdAt(Instant.now()).build();
        List<Customer> customers = List.of(
                Customer.builder()
                        .customerId(UUID.randomUUID())
                        .user(user)
                        .firstName("John")
                        .lastName("Doe")
                        .phone("+233123456789")
                        .isActive(true)
                        .build()
        );

        Page<Customer> page = new PageImpl<>(customers);
        when(customerRepository.searchCustomersByName(query, PageRequest.of(0, 10))).thenReturn(page);

        List<CustomerResponseDto> result = customerService.searchCustomers(query, 10, 0);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("John", result.get(0).getFirstName());
        verify(customerRepository).searchCustomersByName(query, PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("Should return empty list when no customers match search")
    void shouldReturnEmptyListWhenNoCustomersMatchSearch() {
        String query = "nonexistent";

        Page<Customer> page = new PageImpl<>(List.of());
        when(customerRepository.searchCustomersByName(query, PageRequest.of(0, 10))).thenReturn(page);

        List<CustomerResponseDto> result = customerService.searchCustomers(query, 10, 0);

        Assertions.assertEquals(0, result.size());
        verify(customerRepository).searchCustomersByName(query, PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("Should update customer phone successfully")
    void shouldUpdateCustomerPhoneSuccessfully() {
        UUID id = UUID.randomUUID();
        CustomerRequestDto request = new CustomerRequestDto("+233111222333", null);

        User user = User.builder().email("john@example.com").createdAt(Instant.now()).build();
        Customer existing = Customer.builder()
                .customerId(id)
                .user(user)
                .firstName("John")
                .lastName("Doe")
                .phone("+233123456789")
                .isActive(true)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(existing));

        customerService.updateCustomer(id, request);

        verify(customerRepository).findById(id);
        verify(customerRepository).save(argThat(customer ->
                customer.getPhone().equals("+233111222333") &&
                        customer.isActive()
        ));
    }

    @Test
    @DisplayName("Should update customer status successfully")
    void shouldUpdateCustomerStatusSuccessfully() {
        UUID id = UUID.randomUUID();
        CustomerRequestDto request = new CustomerRequestDto(null, false);

        User user = User.builder().email("john@example.com").createdAt(Instant.now()).build();
        Customer existing = Customer.builder()
                .customerId(id)
                .user(user)
                .firstName("John")
                .lastName("Doe")
                .phone("+233123456789")
                .isActive(true)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(existing));

        customerService.updateCustomer(id, request);

        verify(customerRepository).findById(id);
        verify(customerRepository).save(argThat(customer ->
                customer.getPhone().equals("+233123456789") &&
                        !customer.isActive()
        ));
    }

    @Test
    @DisplayName("Should update both phone and status successfully")
    void shouldUpdateBothPhoneAndStatusSuccessfully() {
        UUID id = UUID.randomUUID();
        CustomerRequestDto request = new CustomerRequestDto("+233999888777", false);

        User user = User.builder().email("john@example.com").createdAt(Instant.now()).build();
        Customer existing = Customer.builder()
                .customerId(id)
                .user(user)
                .firstName("John")
                .lastName("Doe")
                .phone("+233123456789")
                .isActive(true)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(existing));

        customerService.updateCustomer(id, request);

        verify(customerRepository).findById(id);
        verify(customerRepository).save(argThat(customer ->
                customer.getPhone().equals("+233999888777") &&
                        !customer.isActive()
        ));
    }

    @Test
    @DisplayName("Should throw error when updating non-existing customer")
    void shouldThrowWhenUpdatingNonExistingCustomer() {
        UUID id = UUID.randomUUID();
        CustomerRequestDto request = new CustomerRequestDto("+233111222333", null);

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.updateCustomer(id, request)
        );

        verify(customerRepository).findById(id);
        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should preserve existing values when only updating phone")
    void shouldPreserveExistingValuesWhenOnlyUpdatingPhone() {
        UUID id = UUID.randomUUID();
        CustomerRequestDto request = new CustomerRequestDto("+233111222333", null);

        User user = User.builder().email("john@example.com").createdAt(Instant.now()).build();
        Customer existing = Customer.builder()
                .customerId(id)
                .user(user)
                .firstName("John")
                .lastName("Doe")
                .phone("+233123456789")
                .isActive(true)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(existing));

        customerService.updateCustomer(id, request);

        verify(customerRepository).save(argThat(customer ->
                customer.getFirstName().equals("John") &&
                        customer.getLastName().equals("Doe") &&
                        customer.isActive()
        ));
    }

    @Test
    @DisplayName("Should preserve existing values when only updating status")
    void shouldPreserveExistingValuesWhenOnlyUpdatingStatus() {
        UUID id = UUID.randomUUID();
        CustomerRequestDto request = new CustomerRequestDto(null, false);

        User user = User.builder().email("jane@example.com").createdAt(Instant.now()).build();
        Customer existing = Customer.builder()
                .customerId(id)
                .user(user)
                .firstName("Jane")
                .lastName("Smith")
                .phone("+233987654321")
                .isActive(true)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(existing));

        customerService.updateCustomer(id, request);

        verify(customerRepository).save(argThat(customer ->
                customer.getFirstName().equals("Jane") &&
                        customer.getLastName().equals("Smith") &&
                        customer.getPhone().equals("+233987654321")
        ));
    }

    @Test
    @DisplayName("Should handle pagination in get all customers")
    void shouldHandlePaginationInGetAllCustomers() {
        User user1 = User.builder().email("customer1@example.com").createdAt(Instant.now()).build();
        User user2 = User.builder().email("customer2@example.com").createdAt(Instant.now()).build();
        List<Customer> customers = List.of(
                Customer.builder()
                        .customerId(UUID.randomUUID())
                        .user(user1)
                        .firstName("Customer1")
                        .lastName("Last1")
                        .phone("+233123456789")
                        .isActive(true)
                        .build(),
                Customer.builder()
                        .customerId(UUID.randomUUID())
                        .user(user2)
                        .firstName("Customer2")
                        .lastName("Last2")
                        .phone("+233987654321")
                        .isActive(true)
                        .build()
        );

        Page<Customer> page = new PageImpl<>(customers);
        when(customerRepository.findAll(PageRequest.of(10, 5))).thenReturn(page);

        List<CustomerResponseDto> result = customerService.getAllCustomers(5, 10);

        Assertions.assertEquals(2, result.size());
        verify(customerRepository).findAll(PageRequest.of(10, 5));
    }

    @Test
    @DisplayName("Should handle pagination in search customers")
    void shouldHandlePaginationInSearchCustomers() {
        String query = "customer";
        User user = User.builder().email("customer1@example.com").createdAt(Instant.now()).build();
        List<Customer> customers = List.of(
                Customer.builder()
                        .customerId(UUID.randomUUID())
                        .user(user)
                        .firstName("Customer1")
                        .lastName("Last1")
                        .phone("+233123456789")
                        .isActive(true)
                        .build()
        );

        Page<Customer> page = new PageImpl<>(customers);
        when(customerRepository.searchCustomersByName(query, PageRequest.of(10, 5))).thenReturn(page);

        List<CustomerResponseDto> result = customerService.searchCustomers(query, 5, 10);

        Assertions.assertEquals(1, result.size());
        verify(customerRepository).searchCustomersByName(query, PageRequest.of(10, 5));
    }

    @Test
    @DisplayName("Should preserve customer id when updating")
    void shouldPreserveCustomerIdWhenUpdating() {
        UUID id = UUID.randomUUID();
        CustomerRequestDto request = new CustomerRequestDto("+233111222333", false);

        User user = User.builder().email("john@example.com").createdAt(Instant.now()).build();
        Customer existing = Customer.builder()
                .customerId(id)
                .user(user)
                .firstName("John")
                .lastName("Doe")
                .phone("+233123456789")
                .isActive(true)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(existing));

        customerService.updateCustomer(id, request);

        verify(customerRepository).save(argThat(customer ->
                customer.getCustomerId().equals(id)
        ));
    }

    @Test
    @DisplayName("Should map customer fields correctly in response")
    void shouldMapCustomerFieldsCorrectlyInResponse() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        User user = User.builder().email("john@example.com").createdAt(createdAt).build();
        Customer customer = Customer.builder()
                .customerId(id)
                .user(user)
                .firstName("John")
                .lastName("Doe")
                .phone("+233123456789")
                .isActive(true)
                .build();

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        CustomerResponseDto response = customerService.getCustomer(id);

        Assertions.assertEquals(id, response.getCustomerId());
        Assertions.assertEquals("John", response.getFirstName());
        Assertions.assertEquals("Doe", response.getLastName());
        Assertions.assertEquals("john@example.com", response.getEmail());
        Assertions.assertEquals("+233123456789", response.getPhone());
        Assertions.assertTrue(response.isActive());
        Assertions.assertEquals(createdAt, response.getCreatedAt());
    }
}
