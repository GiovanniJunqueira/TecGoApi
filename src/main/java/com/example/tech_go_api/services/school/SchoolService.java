package com.example.tech_go_api.services.school;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.tech_go_api.cache.school.SchoolCacheService;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.domain.users.profileadmin.ProfileAdmin;
import com.example.tech_go_api.dto.school.SchoolCreateDTO;
import com.example.tech_go_api.dto.school.SchoolResponseDTO;
import com.example.tech_go_api.dto.school.SchoolUpdateDTO;
import com.example.tech_go_api.exceptions.ConflictException;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.school.SchoolRepository;
import com.example.tech_go_api.services.minio.FileType;
import com.example.tech_go_api.services.minio.MinioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final MinioService minioService;
    private final SchoolCacheService schoolCacheService;


   
   
    public School create(SchoolCreateDTO dto) {
        schoolRepository.findByCnpj(dto.getCnpj()).ifPresent(school -> {
            throw new ConflictException("Escola com este CNPJ já existe");
        });

        School school = new School();
        school.setName(dto.getName());
        school.setCnpj(dto.getCnpj());
        school.setAddress(dto.getAddress());
        school.setCity(dto.getCity());
        
        processLogoUpload(dto.getLogoFile(), school);
        
        School savedSchool = schoolRepository.save(school);
        
        return savedSchool;
    }
    
    
    public SchoolResponseDTO findByUser(User user) {
        return schoolCacheService.findByUser(user);
    }

    public SchoolResponseDTO update(User user, SchoolUpdateDTO dto) {
        if (!(user instanceof ProfileAdmin)) {
            throw new NotFoundException("Usuário não é um administrador de escola");
        }

        ProfileAdmin admin = (ProfileAdmin) user;
        School school = admin.getSchool();
        if (school == null) {
            throw new NotFoundException("Administrador não está associado a nenhuma escola");
        }

        school.setName(dto.getName());
        school.setAddress(dto.getAddress());
        school.setCity(dto.getCity());

        processLogoUpload(dto.getLogoFile(), school);

        schoolRepository.save(school);
        schoolCacheService.evictSchoolCache(user.getId());

        return schoolCacheService.findByUser(user);
    }
    
    
   
   
   
   
   
   
   
   
   
   
    private void processLogoUpload(MultipartFile logoFile, School school) {
        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                String objectName = minioService.uploadFile(logoFile, FileType.LOGO);
                school.setLogoReferenceId(objectName);
                log.info("Logo da escola '{}' salva com sucesso no MinIO: {}", school.getName(), objectName);
            } catch (Exception e) {
                log.error("Erro ao fazer upload da logo para a escola '{}': {}", 
                         school.getName(), e.getMessage(), e);
                throw new RuntimeException("Falha ao fazer upload da logo: " + e.getMessage(), e);
            }
        }
    }
    
   
}
    
