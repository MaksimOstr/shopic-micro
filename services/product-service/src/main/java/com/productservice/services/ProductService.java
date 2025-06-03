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

import java.util.UUID;

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

    @Transactional
    public Product updateProduct(UpdateProductRequest dto, long sellerId, long productId) {
        Product product = productRepository.findBySellerIdAndId(sellerId, productId)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));

        if (dto.name() != null) {
            product.setName(dto.name());
        }

        if(dto.description() != null) {
            product.setDescription(dto.description());
        }

        if(dto.price() != null) {
            product.setPrice(dto.price());
        }

        if(dto.stockQuantity() != null) {
            product.setStockQuantity(dto.stockQuantity());
        }

        if(dto.category() != null) {
            product.setCategory(ProductCategoryEnum.fromString(dto.category()));
        }

        return product;
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
