package com.factoryops.auth.config;

import com.factoryops.auth.entity.Role;
import com.factoryops.auth.entity.RoleName;
import com.factoryops.auth.entity.User;
import com.factoryops.auth.repository.RoleRepository;
import com.factoryops.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Data Seeding...");

        try {
            log.info("Dropping old PostgreSQL CHECK constraint on roles table...");
            jdbcTemplate.execute("ALTER TABLE roles DROP CONSTRAINT IF EXISTS roles_role_name_check");
            
            log.info("Cleaning up old unused roles from the database...");
            jdbcTemplate.execute("DELETE FROM users WHERE role_id IN (SELECT role_id FROM roles WHERE role_name NOT IN ('ADMIN', 'PRODUCTION_MANAGER', 'INVENTORY_MANAGER', 'QUALITY_INSPECTOR', 'SUPPLIER_MANAGER'))");
            jdbcTemplate.execute("DELETE FROM roles WHERE role_name NOT IN ('ADMIN', 'PRODUCTION_MANAGER', 'INVENTORY_MANAGER', 'QUALITY_INSPECTOR', 'SUPPLIER_MANAGER')");
        } catch (Exception e) {
            log.warn("Could not execute constraint/role cleanup. This is normal if the table doesn't exist yet. Error: {}", e.getMessage());
        }
        
        // Seed Roles
        Arrays.stream(RoleName.values()).forEach(roleName -> {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                log.info("Created Role: {}", roleName);
            }
        });

        // Seed Users
        seedUser("admin", "admin@factoryops.com", "Admin User", RoleName.ADMIN);
        seedUser("prod_manager", "prod@factoryops.com", "Production Manager", RoleName.PRODUCTION_MANAGER);
        seedUser("inv_manager", "inv@factoryops.com", "Inventory Manager", RoleName.INVENTORY_MANAGER);
        seedUser("qual_inspector", "qual@factoryops.com", "Quality Inspector", RoleName.QUALITY_INSPECTOR);
        seedUser("sup_manager", "sup@factoryops.com", "Supplier Manager", RoleName.SUPPLIER_MANAGER);

        log.info("Data Seeding Completed!");
    }

    private void seedUser(String username, String email, String fullName, RoleName roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        
        User user = userRepository.findByUsername(username)
                .orElse(User.builder().username(username).build());
        
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password"));
        user.setFullName(fullName);
        user.setPhoneNumber("1234567890");
        user.setActive(true);
        user.setRole(role);
        
        userRepository.save(user);
        log.info("Created/Updated User: {} with Role: {}", username, roleName);
    }
}

