package com.anu.job_tracker.config;

import com.anu.job_tracker.entity.AppUser;
import com.anu.job_tracker.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.AuthenticationException;

@Profile("dev")
@Configuration
public class DevTestUserConfig {

    @Bean
    CommandLineRunner seedJwtTestUser(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            @Value("${TEST_USER_EMAIL:}") String email,
            @Value("${TEST_USER_PASSWORD:}") String rawPassword
    ) {
        return args -> {
            if (email.isBlank() || rawPassword.isBlank()) {
                return;
            }

            AppUser user = appUserRepository.findByEmail(email)
                    .orElseGet(AppUser::new);

            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(rawPassword));

            appUserRepository.save(user);
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(email, rawPassword)
                );
                System.out.println("DEV LOGIN CHECK: SUCCESS");
            } catch (AuthenticationException exception) {
                System.out.println(
                        "DEV LOGIN CHECK: FAILED - "
                                + exception.getClass().getSimpleName()
                );
            }
        };
    }
}
