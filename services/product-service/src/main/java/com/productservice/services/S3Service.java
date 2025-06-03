package com.productservice.services;

import com.productservice.dto.PutObjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final S3AsyncClient s3AsyncClient;

    public CompletableFuture<String> uploadFile(PutObjectDto dto) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(dto.bucket())
                    .key(dto.key())
                    .contentType(dto.file().getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            return s3AsyncClient.putObject(
                    request,
                    AsyncRequestBody.fromBytes(dto.file().getBytes())
            ).thenApply(response -> generateUrl(dto.bucket(), dto.key()))
                    .exceptionally(ex -> {
                        log.error(ex.getMessage());
                        throw new RuntimeException(ex);
                    });
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private String generateUrl(String bucket, String key) {
        return s3AsyncClient.utilities().getUrl(b -> b.bucket(bucket).key(key)).toString();
    }
}
