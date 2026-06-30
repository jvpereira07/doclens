package com.jvpereira.doclens.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public String uploadFile(MultipartFile file) {
        try {
            // Garantir que o bucket existe
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket '{}' criado com sucesso no MinIO.", bucketName);
            }

            // Gerar um nome único para o objeto
            String objectName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            try (InputStream is = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(is, file.getSize(), -1)
                                .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                                .build()
                );
            }

            log.info("Arquivo '{}' salvo com sucesso no bucket '{}'.", objectName, bucketName);
            return bucketName + "/" + objectName;

        } catch (Exception e) {
            log.error("Erro ao fazer upload do arquivo para o MinIO", e);
            throw new RuntimeException("Falha no upload do arquivo para o storage: " + e.getMessage(), e);
        }
    }

    public byte[] downloadFile(String storagePath) {
        if (storagePath == null || !storagePath.contains("/")) {
            log.warn("Tentativa de download com caminho de storage nulo ou inválido: {}", storagePath);
            return new byte[0];
        }
        try {
            int slashIndex = storagePath.indexOf('/');
            String bucket = storagePath.substring(0, slashIndex);
            String object = storagePath.substring(slashIndex + 1);

            try (InputStream stream = minioClient.getObject(
                    io.minio.GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(object)
                            .build())) {
                return stream.readAllBytes();
            }
        } catch (Exception e) {
            log.error("Erro ao fazer download do arquivo do MinIO", e);
            throw new RuntimeException("Falha ao recuperar arquivo do storage: " + e.getMessage(), e);
        }
    }
}
