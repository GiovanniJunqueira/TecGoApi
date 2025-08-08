package com.example.tech_go_api.cache.auth;

import com.example.tech_go_api.domain.users.Role;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.profileadmin.ProfileAdminRepository;
import com.example.tech_go_api.repositories.user.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "meCache")
public class AuthCacheService {

    private final UserRepository repository;
    private final ProfileAdminRepository profileAdminRepository;

    @Cacheable(cacheNames = "meCache", key = "#userId")
    public Object getMe(String userId) {
        log.info("Loading user/profile from DB for id={}", userId);
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado com ID: " + userId));

        if (user.getRole() == Role.ADMIN) {
            return profileAdminRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("Perfil de administrador não encontrado para o usuário: " + userId));
        }

        return user;
    }

    @CacheEvict(cacheNames = "meCache", key = "#userId")
    public void evictMe(String userId) {
    }
}
