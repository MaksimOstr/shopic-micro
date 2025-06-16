package com.productservice.services;

import com.productservice.dto.PutObjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final S3AsyncClient s3AsyncClient;


    public CompletableFuture<String> uploadFile(PutObjectDto dto) {
        try {
            MultipartFile file = dto.file();
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(dto.bucket())
                    .key(dto.key())
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            return s3AsyncClient.putObject(
                            request,
                            AsyncRequestBody.fromBytes(file.getBytes())
                    )
                    .thenApply(_ -> generateUrl(dto.bucket(), dto.key()));
        } catch (IOException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public void delete(String bucket, String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3AsyncClient.deleteObject(deleteObjectRequest);
    }

    private String generateUrl(String bucket, String key) {
        return s3AsyncClient.utilities().getUrl(b -> b.bucket(bucket).key(key)).toString();
    }
}
