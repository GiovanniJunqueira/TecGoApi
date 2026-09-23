package com.example.tech_go_api.services.product;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.tech_go_api.domain.product.Product;
import com.example.tech_go_api.domain.school.School;
import com.example.tech_go_api.domain.staff.Permission;
import com.example.tech_go_api.domain.users.base.User;
import com.example.tech_go_api.dto.product.ProductCreateRequestDTO;
import com.example.tech_go_api.dto.product.ProductResponseDTO;
import com.example.tech_go_api.dto.product.ProductUpdateRequestDTO;
import com.example.tech_go_api.exceptions.NotFoundException;
import com.example.tech_go_api.repositories.product.ProductRepository;
import com.example.tech_go_api.services.minio.FileType;
import com.example.tech_go_api.services.minio.MinioService;
import com.example.tech_go_api.services.school.SchoolResolverService;
import com.example.tech_go_api.services.staff.PermissionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final SchoolResolverService schoolResolverService;
    private final PermissionService permissionService;
    private final MinioService minioService;

    public ProductResponseDTO create(ProductCreateRequestDTO dto, User user) {
        permissionService.requirePermission(user, Permission.PRODUTOS_CRIAR);
        School school = schoolResolverService.schoolOf(user);

        Product product = new Product();
        product.setSchool(school);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setActive(true);
        uploadPhotoIfPresent(dto.getPhotoFile(), product);

        return toResponse(productRepository.save(product));
    }

    public ProductResponseDTO update(String id, ProductUpdateRequestDTO dto, User user) {
        permissionService.requirePermission(user, Permission.PRODUTOS_EDITAR);
        Product product = findOwnedProduct(id, user);

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        uploadPhotoIfPresent(dto.getPhotoFile(), product);

        return toResponse(productRepository.save(product));
    }

    public List<ProductResponseDTO> findAll(User user, boolean active) {
        permissionService.requirePermission(user, Permission.PRODUTOS_VER);
        School school = schoolResolverService.schoolOf(user);
        return productRepository.findBySchoolAndActive(school, active).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO findById(String id, User user) {
        permissionService.requirePermission(user, Permission.PRODUTOS_VER);
        return toResponse(findOwnedProduct(id, user));
    }

    public void deactivate(String id, User user) {
        permissionService.requirePermission(user, Permission.PRODUTOS_INATIVAR);
        Product product = findOwnedProduct(id, user);
        product.setActive(false);
        productRepository.save(product);
    }

    public void reactivate(String id, User user) {
        permissionService.requirePermission(user, Permission.PRODUTOS_REATIVAR);
        Product product = findOwnedProduct(id, user);
        product.setActive(true);
        productRepository.save(product);
    }

    Product findOwnedProduct(String id, User user) {
        School school = schoolResolverService.schoolOf(user);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));

        if (!product.getSchool().getId().equals(school.getId())) {
            throw new IllegalArgumentException("Este produto não pertence à sua escola.");
        }

        return product;
    }

    private void uploadPhotoIfPresent(MultipartFile photoFile, Product product) {
        if (photoFile == null || photoFile.isEmpty()) {
            return;
        }

        try {
            String objectName = minioService.uploadFile(photoFile, FileType.IMAGE);
            product.setPhotoReferenceId(objectName);
            log.info("Foto do produto '{}' salva com sucesso no MinIO: {}", product.getName(), objectName);
        } catch (Exception e) {
            log.error("Erro ao fazer upload da foto para o produto '{}': {}", product.getName(), e.getMessage(), e);
            throw new RuntimeException("Falha ao fazer upload da foto: " + e.getMessage(), e);
        }
    }

    ProductResponseDTO toResponse(Product product) {
        String photoUrl = product.getPhotoReferenceId() != null
                ? minioService.generatePresignedUrl(product.getPhotoReferenceId())
                : null;

        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                photoUrl,
                product.isActive()
        );
    }
}
