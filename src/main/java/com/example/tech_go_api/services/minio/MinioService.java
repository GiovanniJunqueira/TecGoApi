package com.example.tech_go_api.services.minio;

import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.SetBucketPolicyArgs;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MinioService implements InitializingBean {

    private final MinioClient minioClient;
    private final String bucketName;
    private final boolean useSsl;
    private final String endpoint;
    private final int port;

    @Value("${spring.cache.redis.time-to-live}")
    private Duration cacheTtl;
    
    public MinioService(MinioClient minioClient,
                       @Value("${minio.bucketName}") String bucketName,
                       @Value("${minio.use-ssl:false}") boolean useSsl,
                       @Value("${minio.endpoint}") String endpoint,
                       @Value("${minio.port:9000}") int port) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.useSsl = useSsl;
        this.endpoint = endpoint;
        this.port = port;
    }
    
    @Value("${minio.public-url:}")
    private String customPublicUrl;

    @Override
    public void afterPropertiesSet() throws Exception {
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
        
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            log.info("Bucket '{}' criado com sucesso", bucketName);
        }

        String publicPolicy = "{\n" +
                "  \"Version\": \"2012-10-17\",\n" +
                "  \"Statement\": [\n" +
                "    {\n" +
                "      \"Effect\": \"Allow\",\n" +
                "      \"Principal\": {\"AWS\": [\"*\"]},\n" +
                "      \"Action\": [\"s3:GetObject\"],\n" +
                "      \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try {
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(bucketName)
                    .config(publicPolicy)
                    .build());
        } catch (Exception e) {
            log.warn("Não foi possível definir a bucket policy pública para '{}' (o provedor de storage pode não " +
                    "suportar essa operação, ex: Cloudflare R2). Arquivos continuarão acessíveis via URL pré-assinada. Causa: {}",
                    bucketName, e.getMessage());
        }
    }

    public String uploadFile(MultipartFile file, FileType fileType) throws Exception {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            
            String objectName = String.format("%s/%s%s", 
                fileType.getFolderName(), 
                UUID.randomUUID().toString(),
                fileExtension);
            
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            log.info("Arquivo {} enviado com sucesso para {}/{}", 
                originalFilename, bucketName, objectName);
                
            return objectName;
        } catch (Exception e) {
            log.error("Erro ao fazer upload do arquivo: {}", e.getMessage(), e);
            throw new RuntimeException("Falha no upload do arquivo: " + e.getMessage(), e);
        }
    }
    
   
    public String uploadFile(MultipartFile file) throws Exception {
        FileType fileType = FileType.fromMimeType(file.getContentType());
        return uploadFile(file, fileType);
    }

    public InputStream getFile(String objectName) throws Exception {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Erro ao recuperar arquivo: {}", e.getMessage());
            throw new RuntimeException("Falha ao recuperar arquivo: " + e.getMessage(), e);
        }
    }

    public byte[] getFileAsBytes(String objectName) throws Exception {
        try (InputStream stream = getFile(objectName)) {
            return stream.readAllBytes();
        } catch (Exception e) {
            log.error("Erro ao recuperar arquivo: {}", e.getMessage());
            throw new RuntimeException("Falha ao recuperar arquivo: " + e.getMessage(), e);
        }
    }

   
    public String generatePresignedUrl(String objectName) {
        try {
            log.debug("Iniciando geração da URL pré-assinada...");
            log.debug("Parâmetro recebido - objectName: '{}'", objectName);
    
            if (objectName == null || objectName.isEmpty()) {
                log.warn("Nome do objeto é nulo ou vazio");
                return null;
            }
    
            long expirySeconds = cacheTtl.getSeconds();
            log.debug("Tempo de expiração configurado (segundos): {}", expirySeconds);
    
            log.debug("Bucket configurado: '{}'", bucketName);
    
            log.debug("Verificando cliente MinIO: {}", (minioClient != null ? "OK" : "NULO"));
    
            String url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(objectName)
                    .expiry((int) expirySeconds, TimeUnit.SECONDS)
                    .build()
            );
    
            log.debug("URL pré-assinada gerada com sucesso: '{}'", url);
            return url;
    
        } catch (Exception e) {
            log.error("Erro ao gerar URL pré-assinada para objeto '{}' no bucket '{}'", 
                      objectName, bucketName, e);
            return null;
        }
    }
    

    public void deleteFile(String objectName) throws Exception {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("Erro ao excluir arquivo: {}", e.getMessage());
            throw new RuntimeException("Falha ao excluir arquivo: " + e.getMessage(), e);
        }
    }

    public List<Item> listObjects(String prefix, boolean recursive) throws Exception {
        try {
            List<Item> objects = new ArrayList<>();
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(prefix)
                            .recursive(recursive)
                            .build()
            );

            for (Result<Item> result : results) {
                objects.add(result.get());
            }

            return objects;
        } catch (Exception e) {
            log.error("Erro ao listar objetos: {}", e.getMessage());
            throw new RuntimeException("Falha ao listar objetos: " + e.getMessage(), e);
        }
    }

    
    public String getPublicUrl(String objectName) {
        if (objectName == null || objectName.isEmpty()) {
            return null;
        }
        
        if (customPublicUrl != null && !customPublicUrl.isEmpty()) {
            return String.format("%s/%s/%s", 
                customPublicUrl.endsWith("/") ? 
                    customPublicUrl.substring(0, customPublicUrl.length() - 1) : 
                    customPublicUrl,
                bucketName,
                objectName);
        }
        
        String protocol = useSsl ? "https" : "http";
        return String.format("%s://%s:%d/%s/%s", 
            protocol, 
            endpoint, 
            port, 
            bucketName, 
            objectName);
    }
}