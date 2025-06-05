package com.productservice.services;

import com.productservice.dto.PutObjectDto;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.dto.request.UpdateProductRequest;
import com.productservice.entity.Category;
import com.productservice.entity.Product;
import com.productservice.exceptions.NotFoundException;
import com.productservice.projection.ProductImageUrlProjection;
import com.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final S3Service s3Service;
    private final CategoryService categoryService;

    private static final String PRODUCT_IMAGE_BUCKET = "shopic-product-image";
    private static final String PRODUCT_NOT_FOUND = "Product Not Found";

    public Product getProductById(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public CompletableFuture<Product> create(CreateProductRequest dto, MultipartFile productImage, long sellerId) {
        Category category = categoryService.findByName(dto.category());

        return getProductImageUrl(sellerId, productImage).thenApply(url -> {
            Product product = new Product(
                    dto.name(),
                    dto.description(),
                    getSKU(),
                    dto.price(),
                    sellerId,
                    url,
                    category,
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
        Optional.ofNullable(dto.category()).ifPresent(categoryName -> {
            Category category = categoryService.findByName(categoryName);
            product.setCategory(category);
        });

        return product;
    }


    public void updateProductImage(long sellerId, long productId, MultipartFile productImage) {
        String imageUrl = getProductImageUrl(productId, sellerId);
        s3Service.delete(imageUrl);

        PutObjectDto dto = new PutObjectDto(
                PRODUCT_IMAGE_BUCKET,
                getKey(sellerId),
                productImage
        );

        s3Service.uploadFile(dto)
                .thenAccept(newImageUrl -> {
                    int updated = productRepository.updateProductImageUrl(productId, newImageUrl);
                    if (updated == 0) {
                        log.error("Failed to update product image url");
                        throw new NotFoundException(PRODUCT_NOT_FOUND);
                    }
                });
    }

    public Page<Product> getPageOfSellerProducts(long sellerId, Pageable pageable) {
        return productRepository.findBySellerId(sellerId, pageable);
    }




    private String getProductImageUrl(long productId, long sellerId) {
        return productRepository.getProductImageUrl(productId, sellerId)
                .map(ProductImageUrlProjection::getImageUrl)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }


    private CompletableFuture<String> getProductImageUrl(long sellerId, MultipartFile productImage) {
        return s3Service.uploadFile(new PutObjectDto(
                PRODUCT_IMAGE_BUCKET,
                getKey(sellerId),
                productImage
        ));
    }

    private String getKey(long sellerId) {
        return UUID.randomUUID().toString() + sellerId;
    }


    private UUID getSKU() {
        return UUID.randomUUID();
    }
}
