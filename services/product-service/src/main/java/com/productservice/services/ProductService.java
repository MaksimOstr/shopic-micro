package com.productservice.services;

import com.productservice.dto.PutObjectDto;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.entity.Product;
import com.productservice.entity.ProductCategoryEnum;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final S3Service s3Service;

    private static final String PRODUCT_IMAGE_BUCKET = "shopic-product-image";
    private static final String PRODUCT_NOT_FOUND = "Product Not Found";

    public Product getProductById(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }


    public CompletableFuture<Product> create(CreateProductRequest dto, MultipartFile productImage, long sellerId) {
        ProductCategoryEnum productEnum = ProductCategoryEnum.fromString(dto.category());

        return getProductImageUrl(sellerId, productImage).thenApply(url -> {
            Product product = new Product(
                    dto.name(),
                    dto.description(),
                    getSKU(),
                    dto.price(),
                    sellerId,
                    url,
                    productEnum,
                    dto.stockQuantity()
            );

            return productRepository.save(product);
        });
    }

    @Transactional
    public Product updateProduct(UpdateProductRequest dto, long sellerId, long productId) {
        Product product = productRepository.findBySellerIdAndId(sellerId, productId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));

        Optional.ofNullable(dto.name()).ifPresent(product::setName);
        Optional.ofNullable(dto.description()).ifPresent(product::setDescription);
        Optional.ofNullable(dto.price()).ifPresent(product::setPrice);
        Optional.ofNullable(dto.stockQuantity()).ifPresent(product::setStockQuantity);
        Optional.ofNullable(dto.category()).ifPresent(ProductCategoryEnum::fromString);

        return product;
    }

    public String updateProductImage() {
        return "";
    }

    private CompletableFuture<String> getProductImageUrl(long sellerId, MultipartFile productImage) {
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
