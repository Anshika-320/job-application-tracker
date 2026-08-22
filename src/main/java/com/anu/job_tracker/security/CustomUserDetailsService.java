package com.anu.job_tracker.security;

import com.anu.job_tracker.repository.AppUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


    @Service
    public class CustomUserDetailsService implements UserDetailsService {

        private final AppUserRepository appUserRepository;

        public CustomUserDetailsService(AppUserRepository appUserRepository) {
            this.appUserRepository = appUserRepository;
        }

        @Override
        public UserDetails loadUserByUsername(String email)
                throws UsernameNotFoundException {

            return appUserRepository.findByEmail(email)
                    .map(CustomUserDetails::new)
                    .orElseThrow(() ->
                            new UsernameNotFoundException(
                                    "User not found with email: " + email
                            )
                    );
        }
    }

