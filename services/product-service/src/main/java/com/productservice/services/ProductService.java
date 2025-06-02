package com.productservice.services;

import com.productservice.dto.PutObjectDto;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.entity.Product;
import com.productservice.entity.ProductCategoryEnum;
import com.productservice.exceptions.InvalidEnumArgException;
import com.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final S3Service s3Service;

    private static final String PRODUCT_IMAGE_BUCKET = "shopic-product-image";

    public Product create(CreateProductRequest dto, MultipartFile productImage, long sellerId) {
        ProductCategoryEnum productEnum = ProductCategoryEnum.fromString(dto.category());

        Product product = new Product(
                dto.name(),
                dto.description(),
                getSKU(),
                dto.price(),
                sellerId,
                getProductImageUrl(sellerId, productImage),
                productEnum,
                dto.stockQuantity()
        );

        return productRepository.save(product);
    }

    private String getProductImageUrl(long sellerId, MultipartFile productImage) {
        String key = UUID.randomUUID().toString() + sellerId;

        return s3Service.uploadFile(new PutObjectDto(
                PRODUCT_IMAGE_BUCKET,
                key,
                productImage
        ));
    }

    private UUID getSKU() {
        return UUID.randomUUID();
    }
}
