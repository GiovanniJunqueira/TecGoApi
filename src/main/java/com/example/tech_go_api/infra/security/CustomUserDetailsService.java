package com.example.tech_go_api.infra.security;

import com.example.tech_go_api.domain.user.User;
import com.example.tech_go_api.repositories.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = repository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())
        );

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
