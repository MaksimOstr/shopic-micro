package com.productservice.dto;

import org.springframework.web.multipart.MultipartFile;

public record PutObjectDto(
        String bucket,
        String key,
        MultipartFile file
) {}
