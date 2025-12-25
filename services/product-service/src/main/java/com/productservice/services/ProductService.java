package com.productservice.services;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.LikedProductDto;
import com.productservice.dto.ProductBasicInfoDto;
import com.productservice.dto.UserProductDto;
import com.productservice.dto.request.CreateProductRequest;
import com.productservice.entity.Brand;
import com.productservice.entity.Category;
import com.productservice.entity.Product;
import com.productservice.entity.ProductStatusEnum;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.productservice.utils.ProductUtils.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public Product create(CreateProductRequest dto, String imageUrl, Category category, Brand brand) {
        Product product = createProductEntity(dto, imageUrl, category, brand);

        return productRepository.save(product);
    }

    public List<Product> getProductsByIds(Collection<UUID> idList) {
        return productRepository.findByIdIn(idList);
    }


    public boolean existsById(UUID id) {
        return productRepository.existsById(id);
    }

    public Product getActiveProductById(UUID id) {
        return productRepository.findByDeletedAndId(false, id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public Page<Product> getProductPageBySpec(Specification<Product> spec, Pageable pageable) {
        return productRepository.findAll(spec, pageable);
    }

    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PRODUCT_NOT_FOUND));
    }

    public Optional<String> getOptionalProductImageUrl(UUID productId) {
        return productRepository.getProductImageUrl(productId);
    }

    private Product createProductEntity(CreateProductRequest dto, String url, Category category, Brand brand) {
        return Product.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .imageUrl(url)
                .category(category)
                .stockQuantity(dto.stockQuantity())
                .brand(brand)
                .build();
    }
}
