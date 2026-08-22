package com.anu.job_tracker.security;

import com.anu.job_tracker.entity.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

    public class CustomUserDetails implements UserDetails {

        private final AppUser appUser;

        public CustomUserDetails(AppUser appUser) {
            this.appUser = appUser;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of();
        }

        @Override
        public String getPassword() {
            return appUser.getPasswordHash();
        }

        @Override
        public String getUsername() {
            return appUser.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

