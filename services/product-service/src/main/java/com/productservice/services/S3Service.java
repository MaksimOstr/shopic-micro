package com.productservice.services;

import com.productservice.dto.PutObjectDto;
import com.productservice.exceptions.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final S3AsyncClient s3AsyncClient;
    private final S3Client s3Client;


    public String uploadFile(PutObjectDto dto) {
        try {
            MultipartFile file = dto.file();
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(dto.bucket())
                    .key(dto.key())
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(dto.file().getInputStream(), file.getSize())
            );

            return generateUrl(dto.bucket(), dto.key());
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new InternalException(e.getMessage());
        }
    }

    public void delete(String imageUrl) {
        try {
            URI uri = new URI(imageUrl);
            String host = uri.getHost();
            String bucket = host.split("\\.")[0];
            String key = uri.getPath().substring(1);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3AsyncClient.deleteObject(deleteObjectRequest);
        } catch (URISyntaxException e) {
            log.warn(e.getMessage());
        }
    }

    private String generateUrl(String bucket, String key) {
        return s3AsyncClient.utilities().getUrl(b -> b.bucket(bucket).key(key)).toString();
    }
}
