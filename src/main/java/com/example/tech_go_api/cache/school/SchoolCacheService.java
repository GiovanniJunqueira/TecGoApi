package com.example.tech_go_api.cache.school;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.domain.users.profileadmin.ProfileAdmin;
import com.example.tech_go_api.dto.school.SchoolResponseDTO;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.services.minio.MinioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SchoolCacheService {
    private final MinioService  minioService;


    
    @Cacheable(value = "school", key = "#user.id")
    public SchoolResponseDTO findByUser(User user) {
        log.debug("Cache miss - Buscando escola para o usuário: {}", user.getId());
        
        if (!(user instanceof ProfileAdmin)) {
            throw new NotFoundException("Usuário não é um administrador de escola");
        }
        
        ProfileAdmin admin = (ProfileAdmin) user;
        School school = admin.getSchool();
        
        if (school == null) {
            throw new NotFoundException("Administrador não está associado a nenhuma escola");
        }
        
        return this.toResponseDTO(school);
    }
    
   
    @CacheEvict(value = "school", key = "#userId")
    public void evictSchoolCache(String userId) {
        log.debug("Removendo escola do cache para o usuário: {}", userId);
    }

    private final SchoolResponseDTO toResponseDTO(School school) {
        if (school == null) {
            return null;
        }
        
        SchoolResponseDTO dto = new SchoolResponseDTO();
        dto.setId(school.getId());
        dto.setName(school.getName());
        dto.setCnpj(school.getCnpj());
        dto.setAddress(school.getAddress());
        dto.setCity(school.getCity());
        
        if (school.getLogoReferenceId() != null && !school.getLogoReferenceId().isEmpty()) {
            try {
                String presignedUrl = minioService.generatePresignedUrl(school.getLogoReferenceId());
                dto.setLogoUrl(presignedUrl);
                log.debug("URL pré-assinada gerada para a logo da escola '{}': {}", 
                         school.getName(), presignedUrl);
            } catch (Exception e) {
                log.error("Falha ao gerar URL pré-assinada para a logo da escola '{}': {}", 
                         school.getName(), e.getMessage(), e);
            }
        }
        
        return dto;
    }
}
