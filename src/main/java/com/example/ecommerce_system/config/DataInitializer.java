package com.example.ecommerce_system.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        initializeRoles();
    }

    private void initializeRoles() {
        String checkRolesSql = "SELECT COUNT(*) FROM roles";
        Integer count = jdbcTemplate.queryForObject(checkRolesSql, Integer.class);

        if (count != null && count == 0) {
            log.info("Initializing roles...");

            String insertRolesSql = """
                INSERT INTO roles (role_id, role_name, description)
                VALUES 
                    (gen_random_uuid(), 'ADMIN', 'System administrator with full access'),
                    (gen_random_uuid(), 'CUSTOMER', 'Regular customer account')
                """;

            jdbcTemplate.update(insertRolesSql);
            log.info("Roles initialized successfully");
        } else {
            log.info("Roles already exist, skipping initialization");
        }
    }
}
