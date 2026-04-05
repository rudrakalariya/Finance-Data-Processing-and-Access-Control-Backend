package com.finance.config;

import com.finance.model.Role;
import com.finance.model.User;
import com.finance.model.UserStatus;
import com.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            String password = passwordEncoder.encode("password");

            // Create ADMIN
            userRepository.save(User.builder()
                    .username("admin")
                    .email("admin@finance.com")
                    .passwordHash(password)
                    .role(Role.ADMIN)
                    .status(UserStatus.ACTIVE)
                    .deleted(false)
                    .build());

            // Create ANALYST
            userRepository.save(User.builder()
                    .username("analyst")
                    .email("analyst@finance.com")
                    .passwordHash(password)
                    .role(Role.ANALYST)
                    .status(UserStatus.ACTIVE)
                    .deleted(false)
                    .build());

            // Create VIEWER
            userRepository.save(User.builder()
                    .username("viewer")
                    .email("viewer@finance.com")
                    .passwordHash(password)
                    .role(Role.VIEWER)
                    .status(UserStatus.ACTIVE)
                    .deleted(false)
                    .build());
                    
            // Create INACTIVE USER
            userRepository.save(User.builder()
                    .username("inactive")
                    .email("inactive@finance.com")
                    .passwordHash(password)
                    .role(Role.VIEWER)
                    .status(UserStatus.INACTIVE)
                    .deleted(false)
                    .build());
        }
    }
}
