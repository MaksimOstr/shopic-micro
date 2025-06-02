package com.productservice.services;

import com.productservice.dto.PutObjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;

    public String uploadFile(PutObjectDto dto) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(dto.bucket())
                .key(dto.key())
                .contentType(dto.file().getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();


        try(InputStream stream = dto.file().getInputStream()) {
            s3Client.putObject(request, RequestBody.fromInputStream(stream, dto.file().getSize()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "https://" + dto.bucket() + ".s3.amazonaws.com/" + dto.key();
    }
}
